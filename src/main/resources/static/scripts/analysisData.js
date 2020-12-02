peaks = [];
expedData = [];

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

            peaks.push(peak);
		}
    })
    populatePeakList(peaks);
});

var retrievePeakData = function() {
	return peaks;
}

var retrieveExpedData = function() {
    return expedData;
}