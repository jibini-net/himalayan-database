var peakData = [];

$(document).ready(function()
{
    $.get("/peak?filter=COORDSKNOWN+equals+T", function(data)
    {
        var parsed = JSON.parse(data);
        var i;

        for (i = 0; i < parsed.length; i++)
        {
            /* https://www.himalayandatabase.com/downloads/Himalayan%20Database%20Guide.pdf */

            var peak = new Peak();

            peak.name = parsed[i]['PKNAME'];
            peak.coordinates = [parseFloat(parsed[i]['COORDA']), parseFloat(parsed[i]['COORDB'])];
            peak.height = parseFloat(parsed[i]['HEIGHTM']);
            peak.hasBeenClimbed = (parsed[i]['PSTATUS'] == '2');

            peakData.push(peak);

            addElement(peak.coordinates, peakIcon, peak.name);
        }
    });
});


var retrievePeaks = function() {
    return peakData;
}

//TODO: repeat for villages, routes, and expeditions