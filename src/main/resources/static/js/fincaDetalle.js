document.addEventListener("DOMContentLoaded", function () {
    var mapEl = document.getElementById("map");
    if (!mapEl) {
        return; // no hay coordenadas, se mostró el placeholder
    }

    var lat = parseFloat(mapEl.dataset.lat);
    var lng = parseFloat(mapEl.dataset.lng);
    var fincaNombre = mapEl.dataset.name || "Finca";

    if (isNaN(lat) || isNaN(lng)) {
        console.warn("Coordenadas no válidas para el mapa de la finca");
        return;
    }

    var map = L.map("map").setView([lat, lng], 14);

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: "© OpenStreetMap contributors"
    }).addTo(map);

    L.marker([lat, lng]).addTo(map).bindPopup(fincaNombre).openPopup();
});
