var createPie = function(ctx, numSuccess, numFailure) {
	var peakPieGraph = new Chart(ctx, {
		type: 'pie',
		data: {
			datasets: [{
				data: [numSuccess, numFailure],
				backgroundColor: [
					'rgba(50, 168, 82, 1)',
					'rgba(214, 39, 39, 1)'
				]
			}],
			labels: ['Successful Expeditions', 'Failed Expeditions']
		}
	});
}

var createScatter = function(ctx, attemptsPerYear) {
	var points = [];
	for(i = 0; i < attemptsPerYear.length; i++) {
		points[i] = { x: attemptsPerYear[i][0], y: attemptsPerYear[i][1] };
	}
	var peakScatterGraph = new Chart(ctx, {
		type: 'scatter',
		data: {
			datasets: [{
				data: points,
				label: '# of Expeditions per Year'
			}]
		}
	});
}