//Retrieve data from data.js
var peaks = retrievePeaks();

var initializeMap = function() {
    var options = {
        center: [28.5, 83.9],
        minZoom: 6,
        maxBounds: L.latLngBounds(L.latLng(26, 97), L.latLng(39, 73))
    };
    var map = L.map('mapid', options).setView([28.5, 83.9], 6);
    L.tileLayer('https://api.maptiler.com/maps/topo/{z}/{x}/{y}.png?key=tZnptaeI9RvKHsX18rbW', {
        attribution: '<a href="https://www.maptiler.com/copyright/" target="_blank">&copy; MapTiler</a> <a href="https://www.openstreetmap.org/copyright" target="_blank">&copy; OpenStreetMap contributors</a>',
        tileSize: 512,
        zoomOffset: -1
    }).addTo(map);

    return map;
}

var resetMapElements = function() {
    peaks.forEach(peak => addElement(peak.coordinates, peakIcon, peak.name));
    //TODO: add other elements to map here
}

var addElement = function(coordinates, icon, title) {
    L.marker(coordinates, { icon: icon, title: title }).addTo(markers);
}