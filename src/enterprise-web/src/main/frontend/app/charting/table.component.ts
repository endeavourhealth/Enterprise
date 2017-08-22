import {Component, Input, OnInit} from "@angular/core";
import 'highcharts/adapters/standalone-framework.src';
import {Chart} from "./models/Chart";

@Component({
	selector : 'chart-table',
	template : require('./table-component.html')
})
export class TableComponent {

	@Input() data : Chart;
}