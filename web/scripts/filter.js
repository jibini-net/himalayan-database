//where elevation = [minElevation, maxElevation] and climbStatus = [blnClimbed, blnUnclimbed]
var filterAll = function() {
    var minE = document.getElementById('minimumElevation').value;
    if (!minE) { minE = 0; }
    var maxE = document.getElementById('maximumElevation').value;
    if (!maxE) { maxE = 100000; }
    var elevation = [minE, maxE];

    var blnClimbed = document.getElementById('blnClimbedPeaks').checked;
    var blnUnclimbed = document.getElementById('blnUnclimbedPeaks').checked;
    var climbStatus = [blnClimbed, blnUnclimbed];

    peaks.forEach(peak => filterPeak(peak, elevation, climbStatus));
    //Other filters go here
}

var filterPeak = function(peak, elevation, climbStatus) {
    if (peak.height >= elevation[0] && peak.height <= elevation[1]) {
        //Satisfies elevation requirements
        if ((climbStatus[0] == true && peak.hasBeenClimbed == true) ||
            (climbStatus[1] == true && peak.hasBeenClimbed == false)) {
            //Satisfies climbStatus requirements; display peak
            addElement(peak.coordinates, peakIcon, peak.name);
        }
    }
}

//TODO: repeat for villages, routes, and expeditions