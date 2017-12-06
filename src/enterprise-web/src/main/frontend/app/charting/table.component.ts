import {Component, Input, OnInit} from "@angular/core";
import 'highcharts/adapters/standalone-framework.src';
import {Chart} from "./models/Chart";
import {Series} from './models/Series';

@Component({
	selector : 'chart-table',
	template : require('./table-component.html')
})
export class TableComponent {

	@Input() data : Chart;

	getSeriesData(series: Series) {
		if (series.data && series.data.length > 0 && series.data[0].y == null)
			return series.data;

		const result: any[] = [];
		let i = 0;
		for(const category of this.data.categories) {
			if (!series.data[i] || series.data[i].name !== category) {
				result.push(null);
			} else {
				result.push(series.data[i].y);
				i++;
			}
		}

		return result;
	}
}