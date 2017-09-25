import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Chart} from "../../charting/models/Chart";
import {UtilitiesService} from "../utilities.service";
import {Breakdown} from "../models/Breakdown";
import {Series} from "../../charting/models/Series";
import {Filter} from "../models/Filter";
import {linq} from "eds-common-js";
import {PrevInc} from "../models/PrevInc";
import {HealthCareActivityService} from "./healthCareActivity.service";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./healthCareActivityChart.html')
})
export class HealthCareActivityChart implements OnInit {

	public static open(modalService: NgbModal, title : string) {
		const modalRef = modalService.open(HealthCareActivityChart, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.title = title;

		return modalRef;
	}

	@Input() title;

	private prevIncOptions: PrevInc;

	// Graph style - count/per1k/percent
	private graphAs : string = "count";
	private graphsLoaded: number = 0;

	// Breakdown options and current selection
	private breakdownOptions : Breakdown[] = [];
	private breakdown : Breakdown;

	// Filter options and current selection
	private filterGender : Filter[] = [];
	private genders : string[];
	private filterEthnicity : Filter[] = [];
	private ethnicity : string[];
	private filterPostcode : Filter[] = [];
	private postcode : string[];
	private filterLsoa : Filter[] = [];
	private lsoa : string[];
	private filterMsoa : Filter[] = [];
	private msoa : string[];
	private filterAgex10 : Filter[] = [];
	private agex10 : string[];
	private filterOrgs : Filter[] = [];
	private orgs : string[];
	private filterCcg: Filter[] = [];
	private ccgs : string[];

	// Result data
	private activityResults : any;

	// Chart data
	private activityData: Chart;

	// Chrt config
	private height = 500;
	private legend = {align: 'right', layout: 'vertical', verticalAlign: 'middle', width: 100};

	constructor(
		protected $uibModalInstance : NgbActiveModal,
		protected utilService : UtilitiesService,
		protected healthCareActivityService : HealthCareActivityService) {
	}

	ngOnInit(): void {
		this.getReportOptions();
		this.getOptions();
		this.refresh();
	}

	//---------------------------

    getReportOptions() {
        let vm = this;
        vm.healthCareActivityService.getHealthCareActivityOptions()
            .subscribe(
                (result) => {
                    vm.prevIncOptions = result;
                    vm.title = vm.prevIncOptions.title;
                }
            );
    }

	setGraph(graphAs : string) {
		this.graphAs = graphAs;
		this.activityData = this.getChartData('Incidence', this.activityResults, graphAs);
	}

	clear() {
		this.breakdown = this.breakdownOptions[0];
		this.genders = [];
		this.ethnicity = [];
		this.postcode = [];
		this.lsoa = [];
		this.msoa = [];
		this.orgs = [];
		this.agex10 = [];
		this.ccgs = [];
		this.refresh();
	}

	refresh() {
		let vm = this;
		vm.activityData = null;
		vm.healthCareActivityService.getActivityResults(vm.breakdown.field, vm.genders, vm.ethnicity, vm.postcode, vm.lsoa, vm.msoa, vm.orgs, vm.agex10, vm.ccgs)
			.subscribe(
				(results) => {
					// Only load Inc/Prev AFTER we have population (to allow per1k and percentage calculations)
					vm.activityResults = results;
					vm.activityData = vm.getChartData('Population', results, 'count');
				},
				(error) => console.log(error)
			);
	}

	//---------------------------

	private getOptions() {
		this.breakdownOptions = [{ id : 0, name : 'None', field : null, filters : [] }];
		this.getOptionList(1, 'Gender', 'patient_gender_id', this.filterGender);
		this.getOptionList(2, 'Ethnicity', 'ethnic_code', this.filterEthnicity);
		this.getOptionList(3, 'Postcode', 'postcode_prefix', this.filterPostcode);
		this.getOptionList(4, 'LSOA', 'lsoa_code', this.filterLsoa);
		this.getOptionList(5, 'MSOA', 'msoa_code', this.filterMsoa);
		this.getOptionList(6, 'Organisation', 'organisation_id', this.filterOrgs);
		this.getAgeBands(7,'Age band (10 yrs)', 'FLOOR(age_years/10)', 0, 90, 10, this.filterAgex10);
        this.getOptionList(8, 'CCG', 'ccg', this.filterCcg);
		this.breakdown = this.breakdownOptions[0];
	}

	private getOptionList(id : any, title : string, fieldName : string, filter : Filter[]) {
		let vm = this;
		let breakdown = {id: id, name: title, field: fieldName, filters: []};
		this.breakdownOptions.push(breakdown);

		vm.healthCareActivityService.getDistinctValues(fieldName)
			.subscribe(
				(result) => {
					for (let value of result)
						if (value != null)
							filter.push({id: value[0], name: value[1]});
					breakdown.filters = filter;
				}
			);
	}

	private getAgeBands(id : any, title : string, field : string, min : number, max : number, step : number, filter : Filter[]) {
		let breakdown = { id : id, name : title, field : field, filters : [] };
		this.breakdownOptions.push(breakdown);

		let i = min;
		while (i <= max) {
			let band = '';

			if (i==min)
				band = '< '+ (min + step);
			else if (i + step > max)
				band = '> '+ i;
			else
				band = i + '-' + (i+step-1);

			filter.push({id : (i/step).toString(), name: band});
			i+= step;
		}
		breakdown.filters = filter;
	}

	//---------------------------

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
		let filter = this.breakdown.filters.find(f => f.id == series);

		let title = (filter == null) ? 'Unknown' : filter.name;

		let chartSeries : Series = new Series()
			.setName(title)
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
				data.push(result[1]);
			}
		}

		return data;
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

		rowData.push(this.activityData.title);
		rowData = rowData.concat(this.activityData.getRowData())

		let blob = new Blob([rowData.join('\n')], { type: 'text/plain' });
		window['saveAs'](blob, this.title + '.csv');
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
