//TEMPORARY DUMMY DATA
var peakData = [];

var peak1 = new Peak();
peak1.name = 'Peak 1';
peak1.coordinates = [28, 83];
peak1.height = 6000;
peak1.hasBeenClimbed = true;
peakData.push(peak1);

var peak2 = new Peak();
peak2.name = 'Peak 2';
peak2.coordinates = [30, 81];
peak2.height = 8000;
peak2.hasBeenClimbed = false;
peakData.push(peak2);

var peak3 = new Peak();
peak3.name = 'Peak 3';
peak3.coordinates = [29, 85];
peak3.height = 40000;
peak3.hasBeenClimbed = false;
peakData.push(peak3);


var retrievePeaks = function() {
    //TODO: map to Peak objects from DB and return as array of Peak objects
    return peakData;
}

//TODO: repeat for villages, routes, and expeditions