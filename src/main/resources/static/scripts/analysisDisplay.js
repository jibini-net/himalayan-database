var populatePeakList = function(peaks) {
	$("#peakList").empty();
	for (i = 0; i < peaks.length; i++) {
		var moduleClass = "";
		var climbStatus = "";

		// Dynamic elements
		if (i % 2 == 0) {
			moduleClass = "peakModule";
		} else {
			moduleClass = "peakModule alternate";
		}
		if (peaks[i].hasBeenClimbed) {
			climbStatus = "Climbed";
		} else {
			climbStatus = "Unclimbed";
		}

		var innerHTML = "<div id=\"" + peaks[i].id + "\" class=\"" + moduleClass + "\"><div class=\"peakColumn\"><div class=\"peakLabel\">" + peaks[i].name + " - " + peaks[i].height + " m</div><div class=\"successLabel\">" + peaks[i].numSuccessful + " / " + peaks[i].numExped + " Expeditions Successful</div></div><div class=\"peakColumn\"><div class=\"climbedLabel\">Status: " + climbStatus + "</div></div></div>";
		$("#peakList").append(innerHTML);
	}
}