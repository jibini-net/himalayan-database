var selectedPeakId = '';

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

		var id = peaks[i].id;
		var innerHTML = "<div id=\"" + id + "\" class=\"" + moduleClass + "\"><div class=\"peakColumn\"><div class=\"peakLabel\">" + peaks[i].name + " - " + peaks[i].height + " m</div><div class=\"successLabel\">" + peaks[i].numSuccessful + " / " + peaks[i].numExped + " Expeditions Successful</div></div><div class=\"peakColumn\"><div class=\"climbedLabel\">Status: " + climbStatus + "</div></div></div>";
		$("#peakList").append(innerHTML);

		// bind click event
		var element = document.getElementById(id);
		element.addEventListener("click", queryExped.bind(null, id));
	}
}

var populateExpedList = function(expeditions) {
	$('#expedList').empty();
	if(!expeditions.length == 0) {
		for(i = 0; i < expeditions.length; i++) {

			var innerHTML = "<div id=\"" + expeditions[i].id + "\" class=\"expedModule\"></div>";
			$('#expedList').append(innerHTML);

			expedRedraw(expeditions[i], true);
		}
	} else {
		$('#expedList').append("<div class=\"placeholderText\">No registered expeditions</div>");
	}

}

var toggleRoutes = function(expedition) {
	var routeToggleId = "#" + expedition.id + "toggle";
	if($(routeToggleId).hasClass("toggled")) {
		$(routeToggleId).removeClass("toggled").text("Show routes");
		expedRedraw(expedition, false);
	} else {
		for(i = 0; i < expedition.routes.length; i++) {
			$(routeToggleId).addClass("toggled").text("Hide routes");

			// Dynamic elements
			var routeStateClass = "";
			var routeState = "";
			var reasonHTML = "";
			if(expedition.routes[i].routeSuccess == true) {
				routeStateClass = "routeState successText";
				routeState = "Success";
			} else {
				routeStateClass = "routeState activeText";
				routeState = "Failure";
				var reason = "";
				if(expedition.termReason == "Success (main peak)") {
					// Termination reason is not given if one expedition route succeeds
					reason = "Unknown";
				} else {
					reason = expedition.termReason;
				}
				reasonHTML = "<div class=\"reason\">Given reason: " + reason + "</div>";
			}

			var innerHTML = "<div class=\"expedRoute\"><div class=\"routeStateContainer\"><div class=\"" + routeStateClass + "\">Route " + (i + 1) + ": " + routeState + "</div>" + reasonHTML + "</div><div class=\"routeApproach\">Approach: "+ expedition.routes[i].routeApproach +"</div></div>"
			$("#" + expedition.id).append(innerHTML);
		}
	}

}

var expedRedraw = function(expedition, isInitial) {
	if(!isInitial) {
		$("#" + expedition.id).empty();
	}

	// Dynamic elements
	var fatalitiesClass = "";
	var days = "???";
	var toggleId = expedition.id + "toggle";
	var plural = " fatalities";

	if(expedition.fatalities == 0) {
		fatalitiesClass = "fatalitiesLabel nullText";
	} else {
		fatalitiesClass = "fatalitiesLabel activeText";
		if(expedition.fatalities == 1) {
			plural = " fatality";
		}
	}
	if(expedition.expeditionLength !== 0) {
		days = "" + expedition.expeditionLength;
	}

	var innerHTML = "<div class=\"expedTitle\">ID: " + expedition.id + "</div><div class=\"expedInfo\">" + expedition.summitDate + " - " + days + " days, " + expedition.members + " members</div><div class=\"expedContent\"><div class=\"" + fatalitiesClass + "\">" + expedition.fatalities + plural + "</div><div class=\"routesContainer\"><div class=\"routesLabel\">" + expedition.routeSuccesses + " / " + expedition.routeCount + " routes succuessful</div><div id=\"" + toggleId + "\" class=\"routesToggle\">Show routes</div></div></div>";

	if(isInitial) {
		$(innerHTML).appendTo("#" + expedition.id).hide().fadeIn('slow');
	} else {
		$("#" + expedition.id).append(innerHTML);
	}

	// bind route event
	var element = document.getElementById(toggleId);
	element.addEventListener("click", toggleRoutes.bind(null, expedition));
}

var selectPeak = function(peakId) {
	if(selectedPeakId !== '') {
		$("#" + selectedPeakId).removeClass("peakSelected");
	}
	selectedPeakId = peakId;
	$("#" + selectedPeakId).addClass("peakSelected");
}