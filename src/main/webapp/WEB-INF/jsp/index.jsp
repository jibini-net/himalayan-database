<html>
    <head>
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"
            integrity="sha512-xodZBNTC5n17Xt2atTPuE1HxjVMSvLVW9ocqUKLsCC5CXdbqCmblAshOMAS6/keqq/sMZMZ19scR4PsZChSR7A=="
            crossorigin=""/>

        <mvc:resources mapping="/webjars/**" location="/webjars/" />
        <script src="/webjars/jquery/3.5.1/jquery.min.js"></script>

        <link rel="stylesheet" href="stylesheets/site_header.css">
        <link rel="stylesheet" href="stylesheets/elevation_map.css">
	    <link rel="icon" type="image/x-icon" href="images/favicon.ico">

        <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"
            integrity="sha512-XQoYMqMTK8LvdxXYG3nZ448hOEQiglfqkJs1NOQV44cWnUrBc8PkAOcXy20w0vlaXaVUearIOBhiXZ5V3ynxwA=="
            crossorigin=""></script>

        <script src="scripts/peak.js"></script>
        <script src="scripts/data.js"></script>
        <script src="scripts/setup.js"></script>
        <script src="scripts/filter.js"></script>

        <script>
            var peakIcon = L.icon({
                iconUrl: 'images/peak-marker.png'
            });
        </script>
        <title>Visualization of the Himalayan Database</title>
    </head>
    <body>
        <div id="controlBar">
            <img class="siteLogo" src="images/logo.png" />
            <div class="headerText">
                <div class="webTitle">Visualization of the Himalayan Database</div>
                <div class="headerLinks">
                    <div class="headerLinkContainer selected">
                        <div class="headerLink">Map</div>
                    </div>
                    <a class="headerLinkContainer" href="peak-analysis.html">
                        <div class="headerLink">Explore Peaks</div>
                    </a>
                    <a class="headerLinkContainer" href="about.html">
                        <div class="headerLink">About</div>
                    </a>
                </div>
            </div>
        </div>
        <div id="mapid"></div>
        <script>
            var map = initializeMap();
            var markers = L.layerGroup();
            resetMapElements();
            markers.addTo(map);

            var filterControl = L.control({ position: 'topright' });
            filterControl.onAdd = function(map) {
                this._div = L.DomUtil.create('div', 'filterControl');
                this._div.innerHTML = '<div class="options"><div class="filterSettings">Display Settings</div><div class="optionGroup"><div class="option"><div class="optionText">Minimum elevation (m):</div><input id="minimumElevation" class="inputBox" /></div><div class="option"><div class="optionText">Maximum elevation (m):</div><input id="maximumElevation" class="inputBox" /></div></div><div class="optionGroup"><div class="option"><div class="optionText">Include climbed peaks:</div><input id="blnClimbedPeaks" type="checkbox" checked/></div><div class="option"><div class="optionText">Include unclimbed peaks:</div><input id="blnUnclimbedPeaks" type="checkbox" checked/></div></div><button onclick="onFilter()" id="btnFilter" type="button">Filter</button></div>';
                return this._div;
            }
            filterControl.addTo(map);

            var onFilter = function() {
                markers.remove();
                markers = L.layerGroup().addTo(map);
                filterAll();
            }
        </script>
    </body>
</html>