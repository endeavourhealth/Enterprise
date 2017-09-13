import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Chart} from "../charting/models/Chart";
import {UtilitiesService} from "./utilities.service";
import {Breakdown} from "./models/Breakdown";
import {Series} from "../charting/models/Series";
import {Filter} from "./models/Filter";
import {linq} from "eds-common-js";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./prevIncChart-dialog.html')
})
export class PrevIncChartDialog implements OnInit {

	public static open(modalService: NgbModal, title : string) {
		const modalRef = modalService.open(PrevIncChartDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.title = title;

		return modalRef;
	}

	@Input() title;

	private breakdown : Breakdown;
	private breakdownOptions : Breakdown[] = [];

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

	private incChart: Chart;
	private prevChart: Chart;

	private multiSelectSettings = {
		enableSearch: true,
		checkedStyle: 'fontawesome',
		buttonClasses: 'form-control text-left',
		dynamicTitleMaxItems: 1,
		displayAllSelectedText: true,
		showCheckAll: true,
		showUncheckAll: true,
		closeOnClickOutside: true
	};

	private multiSelectTexts = {
		checkAll: 'Select all',
		uncheckAll: 'Unselect all',
		checked: 'item',
		checkedPlural: 'items',
		searchPlaceholder: 'Find',
		searchEmptyResult: 'Nothing found...',
		searchNoRenderText: 'Type in search box to see results...',
		defaultTitle: 'Select',
		allSelected: 'All',
	};

	private colors = ['LightBlue', 'Plum', 'Yellow', 'LightSalmon'];						// Male, Female, Other, Total
	private height = 200;
	private legend = {align: 'right', layout: 'vertical', verticalAlign: 'middle', width: 100};

	constructor(protected $uibModalInstance : NgbActiveModal, protected utilService : UtilitiesService) {
	}

	ngOnInit(): void {
		this.getOptions();
		this.refresh();
	}

	getOptions() {
		this.breakdownOptions = [{ id : 0, name : 'None', field : null, filters : [] }];
		this.getOptionList(1, 'Gender', 'patient_gender_id', this.filterGender);
		this.getOptionList(2, 'Ethnicity', 'ethnic_code', this.filterEthnicity);
		this.getOptionList(3, 'Postcode', 'postcode_prefix', this.filterPostcode);
		this.getOptionList(4, 'LSOA', 'lsoa_code', this.filterLsoa);
		this.getOptionList(5, 'MSOA', 'msoa_code', this.filterMsoa);
		this.getOptionList(6, 'Organisation', 'organisation_id', this.filterOrgs);
		this.getAgeBands(7,'Age band (10 yrs)', 'FLOOR(age_years/10)', 0, 90, 10, this.filterAgex10);
		this.breakdown = this.breakdownOptions[0];
	}

	getOptionList(id : any, title : string, fieldName : string, filter : Filter[]) {
		let vm = this;
		let breakdown = {id: id, name: title, field: fieldName, filters: []};
		this.breakdownOptions.push(breakdown);

		vm.utilService.getDistinctValues(fieldName)
			.subscribe(
				(result) => {
					for (let value of result)
						if (value != null)
							filter.push({id: value[0], name: value[1]});
					breakdown.filters = filter;
				}
			);
	}

	getAgeBands(id : any, title : string, field : string, min : number, max : number, step : number, filter : Filter[]) {
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

	clear() {
		this.breakdown = this.breakdownOptions[0];
		this.genders = [];
		this.ethnicity = [];
		this.postcode = [];
		this.lsoa = [];
		this.msoa = [];
		this.orgs = [];
		this.agex10 = [];
		this.refresh();
	}

	refresh() {
		this.incChart = null;
		this.prevChart = null;
		let vm = this;
		vm.utilService.getIncidenceResults(vm.breakdown.field, vm.genders, vm.ethnicity, vm.postcode, vm.lsoa, vm.msoa, vm.orgs, vm.agex10)
			.subscribe(
				(results) => vm.incChart = this.getChartData('Incidence', results),
				(error) => console.log(error)
			);

		vm.utilService.getPrevalenceResults(vm.breakdown.field, vm.genders, vm.ethnicity, vm.postcode, vm.lsoa, vm.msoa, vm.orgs, vm.agex10)
			.subscribe(
				(results) => vm.prevChart = this.getChartData('Prevalence', results),
				(error) => console.log(error)
			);
	}

	//---------------------------

	getChartData(title : string, results: any) {
		if (results[0].length == 3)
			return this.getGroupedChartData(title, results);

		return this.getTotalChartData(title, results);
	}

	getTotalChartData(title : string, results : any) {
		let categories : string[] = linq(results).Select(row => row[0]).ToArray();
		let data : string[] = linq(results).Select(row => row[1]).ToArray();

		return new Chart()
			.setCategories(categories)
			.setColors(this.colors)
			.setHeight(this.height)
			.setLegend(this.legend)
			.setTitle(title)
			.addYAxis(title, false)
			.setSeries([
				new Series()
					.setName('Total')
					.setType('spline')
					.setData(data)
			]);
	}

	getGroupedChartData(title : string, results : any) {
		let categories : string[] = linq(results)
			.Select(row => row[0])
			.Distinct()
			.ToArray()
			.sort();

		let groupedResults = linq(results)
			.GroupBy(r => r[2], r => r);

		let chartSeries : Series[] = linq(Object.keys(groupedResults))
			.Select(key => this.createSeriesChart(key, categories, groupedResults[key]))
			.ToArray();

		return new Chart()
			.setCategories(categories)
			.setColors(this.colors)
			.setHeight(this.height)
			.setLegend(this.legend)
			.setTitle(title)
			.addYAxis(title, false)
			.setSeries(chartSeries);
	}

	createSeriesChart(series : string, categories : string[], results : any) : Series {
		let filter = this.breakdown.filters.find(f => f.id == series);

		let title = (filter == null) ? 'Unknown' : filter.name;

		let chartSeries : Series = new Series()
			.setName(title)
			.setType('spline');

		let data : number[] = [];

		for (let category of categories) {
			let result = linq(results).Where(r => category == r[0]).SingleOrDefault();
			if (result && result[1] != null)
				data.push(result[1]);
			else
				data.push(0);
		}

		chartSeries.setData(data);

		return chartSeries;
	}

	//----------------------------

	export() {

		let rowData = [];

		rowData.push(this.incChart.title);
		rowData = rowData.concat(this.incChart.getRowData())

		let blob = new Blob([rowData.join('\n')], { type: 'text/plain' });
		window['saveAs'](blob, this.title + '.csv');
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
