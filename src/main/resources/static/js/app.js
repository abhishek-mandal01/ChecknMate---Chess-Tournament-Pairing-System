function handleTournamentClick(tournamentId) {
    localStorage.setItem('selectedTournamentId', tournamentId);
    apiRequest('/api/players/by-tournament?tournamentId=' + encodeURIComponent(tournamentId))
        .then(function(players) {
            if (Array.isArray(players) && players.length > 0) {
                window.location.href = 'standings.html';

            } else {
                window.location.href = 'players.html';
            }
        })
        .catch(function() {
            window.location.href = 'players.html';
        });
}
function showToast(message, type) {
    var container = document.getElementById("toastContainer");
    if (!container) return;

    var tone = type || "info";
    var toast = document.createElement("div");
    toast.className = "toast " + tone;
    toast.textContent = message;
    container.appendChild(toast);

    setTimeout(function () {
        toast.style.opacity = "0";
        toast.style.transform = "translateX(120%)";
        setTimeout(function () {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 250);
    }, 2600);
}

function escapeHtml(value) {
    if (value === null || value === undefined) return "";
    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/\"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

function openModal(modalId) {
    var modal = document.getElementById(modalId);
    if (!modal) return;
    modal.classList.add("active");
}

function closeModal(modalId) {
    var modal = document.getElementById(modalId);
    if (!modal) return;
    modal.classList.remove("active");
}

function parseJsonResponse(response) {
    return response.text().then(function (text) {
        if (!text) return {};
        try {
            return JSON.parse(text);
        } catch (e) {
            return { error: "Invalid response from server." };
        }
    });
}

function apiRequest(url, options) {
    return fetch(url, options || {}).then(function (response) {
        return parseJsonResponse(response).then(function (data) {
            if (!response.ok) {
                throw new Error(data.error || "Request failed.");
            }
            return data;
        });
    });
}

function setupMobileSidebar() {
    var toggle = document.getElementById("mobileToggle");
    var sidebar = document.getElementById("sidebar");
    if (!toggle || !sidebar) return;

    toggle.addEventListener("click", function () {
        sidebar.classList.toggle("open");
    });
}



// Minimal CSS for CSV preview table (inject if not present)
if (!document.getElementById('csv-table-style')) {
    var style = document.createElement('style');
    style.id = 'csv-table-style';
    style.innerHTML = '.csv-table { border-collapse: collapse; width: 100%; margin-bottom: 8px; } .csv-table th, .csv-table td { border: 1px solid #ccc; padding: 4px 8px; text-align: left; } .csv-table th { background: #f5f5f5; }';
    document.head.appendChild(style);
}

function setupModalDismiss() {
    var overlays = document.querySelectorAll(".modal-overlay");
    for (var i = 0; i < overlays.length; i++) {
        overlays[i].addEventListener("click", function (event) {
            if (event.target === this && this.id) {
                closeModal(this.id);
            }
        });
    }

    document.addEventListener("keydown", function (event) {
        if (event.key !== "Escape") return;
        var activeModals = document.querySelectorAll(".modal-overlay.active");
        for (var i = 0; i < activeModals.length; i++) {
            activeModals[i].classList.remove("active");
        }
    });
}

document.addEventListener("DOMContentLoaded", function () {
    setupMobileSidebar();
    setupModalDismiss();
});