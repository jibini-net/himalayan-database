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
		return (a.numExped / a.numSuccessful) >= (b.numExped / b.numSuccessful) ? 1 : -1;
	});
	return peaks;
}