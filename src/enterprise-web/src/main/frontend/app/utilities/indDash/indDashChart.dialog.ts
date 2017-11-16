import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Chart} from "../../charting/models/Chart";
import {Series} from "../../charting/models/Series";
import {Filter} from "../models/Filter";
import {linq} from "eds-common-js";
import {IndDash} from "../models/IndDash";
import {IndDashService} from "./indDash.service";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./indDashChart-dialog.html')
})
export class IndDashChartDialog implements OnInit {

	public static open(modalService: NgbModal, title : string) {
		const modalRef = modalService.open(IndDashChartDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.title = title;

		return modalRef;
	}

	@Input() title;

	// Graph style - count/per1k/percent
	private graphAs : string = "count";
	private graphsLoaded: number = 0;

	// Filter options and current selection
	private filterIndicators : Filter[] = [];
	private indicators : string[];
	private filterOrgs : Filter[] = [];
	private orgs : string[];
	private filterCcg: Filter[] = [];
	private ccgs : string[];

	// Result data
	private populationCounts : any[];

	// Chart data
	private popChart: Chart;

	private indicator: string = "0";

	// Chrt config
	private height = 200;
	private legend = {align: 'right', layout: 'vertical', verticalAlign: 'middle', width: 100};

	constructor(protected $uibModalInstance : NgbActiveModal, protected indDashService : IndDashService) {
	}

	ngOnInit(): void {
		this.refresh();
	}


	clear() {
		this.indicators = [];
		this.orgs = [];
		this.ccgs = [];
		this.refresh();
	}

	refresh() {
		this.popChart = null;
		this.graphsLoaded = 0;

		// Get the populations first (per1k and percent charts need this data)
		this.loadPopulationResults();
	}

	indDash() {
		if (this.indicators[0]=="1"&&this.indicators[1]=="2"&&this.indicators[2]=="3"&&this.indicators[3]=="4")
			this.indicator = "5";
		else if (this.indicators[0]=="1")
			this.indicator = "1";
		else if (this.indicators[0]=="2")
			this.indicator = "2";
		else if (this.indicators[0]=="3")
			this.indicator = "3";
		else if (this.indicators[0]=="4")
			this.indicator = "4";
	}

	private loadPopulationResults() {
		let vm = this;
		this.filterIndicators = [
			{id:'1', name:'DM002 - Diabetes mellitus, last BP 150/90 mmHg or less'},
			{id:'2', name: 'DM003 - Diabetes mellitus, last BP 140/80 mmHg or less'},
			{id:'3', name: 'DM004 - Diabetes mellitus, last total cholesterol 5 mmol/l or less'},
			{id:'4', name: 'DM005 - Diabetes mellitus, record of an albumin:creatinine ratio test in the preceding 12 months'}
		];
		this.filterOrgs = [
			{id:'1', name:'Chrisp Street Health Centre'},
			{id:'2', name: 'St Pauls Way Medical Centre'},
			{id:'3', name: 'Shoreditch Park Surgery'},
			{id:'4', name: 'Harford Health Centre'},
			{id:'5', name: 'Globe Town Surgery'},

		];
		this.filterCcg = [
			{id:'1', name:'Tower Hamlets'},
			{id:'2', name: 'Newham'},
			{id:'3', name: 'City and Hackney'},
			{id:'4', name: 'Waltham Forest'}
		];

	}

	private getChartData(title : string, results: any, graphAs : string) {
		if (results[0].length == 3)
			return this.getGroupedChartData(title, results, graphAs);

		return this.getTotalChartData(title, results, graphAs);
	}

	private getTotalChartData(title : string, results : any, graphAs : string) {
		let categories : string[] = linq(results).Select(row => row[0]).ToArray();
		let data : number[] = this.getSeriesData(categories, results, graphAs);

		return new Chart()
			.setCategories(categories)
			.setHeight(this.height)
			.setLegend(this.legend)
			//.setTitle(title)
			.addYAxis(title + this.getGraphAsSuffix(graphAs), false)
			.setSeries([
				new Series()
					.setName('Total '+this.getGraphAsSuffix(graphAs))
					.setType('spline')
					.setData(data)
			]);
	}

	private getGraphAsSuffix(graphAs : string) {
		if (graphAs == 'percent')
			return ' (%)';
		else if (graphAs == 'per1k')
			return ' (/1k)';
		else
			return '';
	}

	private getGroupedChartData(title : string, results : any, graphAs : string) {
		let categories : string[] = linq(results)
			.Select(row => row[0])
			.Distinct()
			.ToArray()
			.sort();

		let groupedResults = linq(results)
			.Where(r => r[2] != 'Unknown')
			.GroupBy(r => r[2], r => r);

		let chartSeries : Series[] = linq(Object.keys(groupedResults))
			.Select(key => this.createSeriesChart(key, categories, groupedResults[key], graphAs))
			.ToArray();

		return new Chart()
			.setCategories(categories)
			.setHeight(this.height)
			.setLegend(this.legend)
			//.setTitle(title)
			.addYAxis(title + this.getGraphAsSuffix(graphAs), false)
			.setSeries(chartSeries);
	}

	private createSeriesChart(series : string, categories : string[], results : any, graphAs : string) : Series {
		let chartSeries : Series = new Series()
			.setName("test")
			.setType('spline');

		let data : number[] = this.getSeriesData(categories, results, graphAs, series);

		chartSeries.setData(data);

		return chartSeries;
	}

	private getSeriesData(categories : string[], results : any, graphAs : string, series? : string) {
		let data : number[] = [];

		for (let category of categories) {
			let result = linq(results).Where(r => category == r[0]).SingleOrDefault();
			if (!result || result[1] == null)
				data.push(0);
			else {
				data.push(this.getSeriesDataGraphAsValue(graphAs, result, category, series));
			}
		}

		return data;
	}

	private getSeriesDataGraphAsValue(graphAs: string, result: any, category, series: string) {
		if (graphAs == 'count')
			return result[1];

		let population = this.getPopulationForCategoryAndSeries(category, series);

		if (graphAs == 'per1k')
			return (this.round(result[1] * 1000 / population[1], 2));
		else if (graphAs == 'percent')
			return (this.round(result[1] * 100 / population[1], 2));
	}

	private getPopulationForCategoryAndSeries(category, series : string) {
		return linq(this.populationCounts)
			.Where(r => category == r[0] && (!series || (series == r[2] || r[2] == null)))
			.SingleOrDefault();
	}

	private round(n : number, dp : number) {
		if (dp == 0)
			return n;

		let factor = Math.pow(10,dp);
		return Math.round(n * factor) / factor;
	}

	//----------------------------

	export() {

		let rowData = [];

		let blob = new Blob([rowData.join('\n')], { type: 'text/plain' });
		window['saveAs'](blob, this.title + '.csv');
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
