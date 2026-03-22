package com.chess_pairing.chess_tournament.repository;

import java.io.InputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseUtil {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/chess_tournament";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";
    private static final String DEFAULT_DRIVER = "org.postgresql.Driver";

    private static final String dbUrl;
    private static final String dbUser;
    private static final String dbPassword;
    private static final String dbDriver;

    private static boolean schemaInitialized = false;

    static {
        Properties properties = new Properties();
        try (InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load application.properties. Using defaults.");
        }

        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USERNAME");
        String envPassword = System.getenv("DB_PASSWORD");

        String propUrl = resolvePropertyValue(properties.getProperty("spring.datasource.url"));
        String propUser = resolvePropertyValue(properties.getProperty("spring.datasource.username"));
        String propPassword = resolvePropertyValue(properties.getProperty("spring.datasource.password"));
        String propDriver = resolvePropertyValue(properties.getProperty("spring.datasource.driver-class-name"));

        String resolvedUrl = pickFirst(envUrl, propUrl, DEFAULT_URL);
        String resolvedUser = pickFirst(envUser, propUser, DEFAULT_USER);
        String resolvedPassword = pickFirst(envPassword, propPassword, DEFAULT_PASSWORD);

        ConnectionConfig config = normalizeConnectionConfig(resolvedUrl, resolvedUser, resolvedPassword);
        dbUrl = config.url;
        dbUser = config.user;
        dbPassword = config.password;
        dbDriver = pickFirst(null, propDriver, DEFAULT_DRIVER);

        try {
            Class.forName(dbDriver);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JDBC driver: " + dbDriver, e);
        }
    }

    private static String pickFirst(String first, String second, String fallback) {
        if (first != null && !first.trim().isEmpty()) return first.trim();
        if (second != null && !second.trim().isEmpty()) return second.trim();
        return fallback;
    }

    private static String resolvePropertyValue(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (!trimmed.startsWith("${") || !trimmed.endsWith("}")) {
            return trimmed;
        }

        String inner = trimmed.substring(2, trimmed.length() - 1);
        int colonIndex = inner.indexOf(':');
        if (colonIndex < 0) {
            String envOnly = System.getenv(inner);
            return (envOnly != null && !envOnly.trim().isEmpty()) ? envOnly.trim() : null;
        }

        String envKey = inner.substring(0, colonIndex);
        String defaultValue = inner.substring(colonIndex + 1);
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }
        return defaultValue.trim();
    }

    private static ConnectionConfig normalizeConnectionConfig(String url, String user, String password) {
        if (url == null) {
            return new ConnectionConfig(DEFAULT_URL, user, password);
        }

        String trimmedUrl = url.trim();
        if (trimmedUrl.startsWith("jdbc:postgresql://")) {
            return new ConnectionConfig(trimmedUrl, user, password);
        }

        if (!trimmedUrl.startsWith("postgresql://")) {
            return new ConnectionConfig(trimmedUrl, user, password);
        }

        URI uri = URI.create(trimmedUrl);
        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getRawPath() == null ? "" : uri.getRawPath();
        String query = uri.getRawQuery();

        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://");
        jdbcUrl.append(host);
        if (port > 0) {
            jdbcUrl.append(':').append(port);
        }
        jdbcUrl.append(path);
        if (query != null && !query.isEmpty()) {
            jdbcUrl.append('?').append(query);
        }

        String parsedUser = user;
        String parsedPassword = password;
        String userInfo = uri.getUserInfo();
        if (userInfo != null && !userInfo.isEmpty()) {
            String[] creds = userInfo.split(":", 2);
            if (creds.length > 0 && !creds[0].trim().isEmpty()) {
                parsedUser = creds[0].trim();
            }
            if (creds.length > 1 && !creds[1].trim().isEmpty()) {
                parsedPassword = creds[1].trim();
            }
        }

        return new ConnectionConfig(jdbcUrl.toString(), parsedUser, parsedPassword);
    }

    private static class ConnectionConfig {
        private final String url;
        private final String user;
        private final String password;

        private ConnectionConfig(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public static synchronized void initializeSchema() {
        if (schemaInitialized) {
            return;
        }

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS tournaments (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "format VARCHAR(100) NOT NULL, " +
                    "total_rounds INTEGER, " +
                "status VARCHAR(50), " +
                "tournament_date VARCHAR(30), " +
                "location VARCHAR(255))");

            // Backward-compatible migration for existing databases.
            stmt.execute("ALTER TABLE tournaments ADD COLUMN IF NOT EXISTS tournament_date VARCHAR(30)");
            stmt.execute("ALTER TABLE tournaments ADD COLUMN IF NOT EXISTS location VARCHAR(255)");

            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "id SERIAL PRIMARY KEY, " +
                    "tournament_id INTEGER REFERENCES tournaments(id) ON DELETE CASCADE, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "rating INTEGER)");

            stmt.execute("CREATE TABLE IF NOT EXISTS matches (" +
                    "id SERIAL PRIMARY KEY, " +
                    "tournament_id INTEGER REFERENCES tournaments(id) ON DELETE CASCADE, " +
                    "round_num INTEGER, " +
                    "board_num INTEGER, " +
                    "white_player_id INTEGER REFERENCES players(id) ON DELETE SET NULL, " +
                    "black_player_id INTEGER REFERENCES players(id) ON DELETE SET NULL, " +
                    "result VARCHAR(20))");

            schemaInitialized = true;
        } catch (Exception e) {
            throw new RuntimeException("Error initializing PostgreSQL schema: " + e.getMessage(), e);
        }
    }
}
