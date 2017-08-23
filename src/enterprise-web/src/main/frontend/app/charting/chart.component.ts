import {Component, Input, OnInit} from "@angular/core";
import 'highcharts/adapters/standalone-framework.src';
import {Chart} from "./models/Chart";

const Highcharts = require('highcharts/highcharts.src');

@Component({
	selector : 'chart',
	template : require('./chart-component.html')
})
export class ChartComponent {
	chart : any;

	@Input()
	set data(data : Chart) {

		let config = {
			title : { text : data.title },
			xAxis : { categories : data.categories },
			yAxis : data.yAxis,
			series : data.series
		};

		this.chart = Highcharts.chart('chart-placeholder', config);

		let vm = this;
		setTimeout(() => vm.chart.reflow(), 1);
	}
}