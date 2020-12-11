// TODO:  Move to backend query?

var onAnalysisFilter = function() {
	var unfilteredList = retrievePeakData();

	// search filter
	var searchPhrase = $("#searchFilter").val().toLowerCase();
	var filteredList = unfilteredList.filter(peak => peak.name.toLowerCase().indexOf(searchPhrase) !== -1);

	// dropdown order
	if($("#contentFilter").val() == "height") {
		filteredList = sortHeight(filteredList);
	}
	if($("#contentFilter").val() == "success") {
		filteredList = sortSuccessRate(filteredList);
	}
	// filteredList sorted by name by default

	populatePeakList(filteredList);
}

var sortHeight = function(peaks) {
	peaks.sort((a, b) => b.height - a.height);
	return peaks;
}

var sortSuccessRate = function(peaks) {
	peaks.sort((a, b) => {
		if(a.numExped == 0) { return 1; }
		if(b.numExped == 0) { return -1; }

		if((a.numSuccessful / a.numExped) > (b.numSuccessful / b.numExped)) { return -1; }
		else if((a.numSuccessful / a.numExped) < (b.numSuccessful / b.numExped)) { return 1; }
		else {
			if(a.numExped > b.numExped) { return -1; }
			else { return 1; }
		}
	});
	return peaks;
}