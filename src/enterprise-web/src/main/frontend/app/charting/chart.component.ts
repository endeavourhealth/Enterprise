import {Component, Input, OnInit, ViewChild} from "@angular/core";
import 'highcharts/adapters/standalone-framework.src';
import {Chart} from "./models/Chart";

const Highcharts = require('highcharts/highcharts.src');

@Component({
	selector : 'chart',
	template : require('./chart-component.html')
})
export class ChartComponent {
	chart : any;

	@ViewChild('chartPlaceholder') chartPlaceholder;

	@Input()
	set data(data : Chart) {

		if (data) {
			let config = {
				chart: {height: data.height},
//				colors: data.colors,
				title: {text: data.title},
				xAxis: {categories: data.categories},
				yAxis: data.yAxis,
				series: data.series,
				legend: data.legend
			};

			this.chart = Highcharts.chart(this.chartPlaceholder.nativeElement, config);

			let vm = this;
			setTimeout(() => vm.chart.reflow(), 1);
		}
	}
}