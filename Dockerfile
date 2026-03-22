# Use official Eclipse Temurin OpenJDK 17 image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY . .

# Make Maven wrapper executable (for Linux builds)
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package

# Expose the port your app runs on (default 8081)
EXPOSE 8081

# Run the application (update JAR name if needed)
CMD ["java", "-jar", "target/chess-tournament-0.0.1-SNAPSHOT.jar"]
