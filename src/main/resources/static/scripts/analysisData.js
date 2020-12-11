peaks = [];

$(document).ready(function() {
    $.get("/peak-analysis", function(data) {
        var parsed = JSON.parse(data);
        for(i = 0; i < parsed.length; i++) {
            var peak = new Peak();

            peak.id = parsed[i]['peak-id'];
            peak.name = parsed[i]['peak-name'];
            peak.height = parsed[i]['peak-height'];
            peak.hasBeenClimbed = parsed[i]['peak-climbed'];
            peak.numExped = parsed[i]['num-expeditions'];
            peak.numSuccessful = parsed[i]['num-successful'];
            peak.attempts = queryAttempts(peak.id);

            peaks.push(peak);
        }
        populatePeakList(peaks);
    });
});

var queryExped = function(peakId) {
    $('#expedList').empty();
    $('#expedList').append("<div class=\"placeholderText\">Loading...</div>");
    selectPeak(peakId);

    $.get("/peak-analysis/" + peakId, function(data) {
        var expedData = [];
        var parsed = JSON.parse(data);

        for(i = 0; i < parsed.length; i++) {
            var expedition = new Expedition();

            expedition.id = parsed[i]['expedition-id']
            expedition.fatalities = parsed[i]['member-deaths'];
            expedition.summitDate = parsed[i]['summit-date'];
            expedition.year = parsed[i]['year'];
            expedition.expeditionLength = parsed[i]['total-days'];
            expedition.members = parsed[i]['total-members'];
            expedition.termReason = parsed[i]['termination-reason'];

            var routes = [];
            var routeCount = parsed[i]['routes'].length;
            var routeSuccesses = 0;
            for(j = 0; j < routeCount; j++) {
                var route = new Route();
                route.routeApproach = parsed[i]['routes'][j]['route'];
                route.routeSuccess = parsed[i]['routes'][j]['success'];
                if(route.routeSuccess == true) { routeSuccesses++; }
                routes.push(route);
            }
            expedition.routes = routes;
            expedition.routeCount = routeCount;
            expedition.routeSuccesses = routeSuccesses;

            expedData.push(expedition);
		}
        populateExpedList(expedData);
    })
}

var queryAttempts = function(peakId) {
    $.get("/peak-analysis/" + peakId, function(data) {
        var parsed = JSON.parse(data);

        // attempts as an array of [year, expeditionCount]
        attempts = [];
        attempts.push([parsed[0]['year'], 1]);
        for(i = 1; i < parsed.length; i++) {
            var year = parsed[i]['year'];
            var newYear = true;
            for(j = 0; j < parsed.length; j++) {
                if(j[0] == year) {
                    j[1]++;
                    newYear = false;
				}
            }
            if(newYear == true) {
                attempts.push([year, 1]);
			}
		}
        return attempts;
    })
}

var retrievePeakData = function() {
    return peaks;
}