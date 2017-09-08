import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Chart} from "../charting/models/Chart";
import {UtilitiesService} from "./utilities.service";
import {Breakdown} from "./models/Breakdown";
import {Series} from "../charting/models/Series";
import {Filter} from "./models/Filter";

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

	private chart: Chart;

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
	private colors = ['LightBlue', 'Plum', 'Yellow', 'LightSalmon'];						// Male, Female, Other, Total
	private height = 400;
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
		this.getAgeBands(6,'Age band (10 yrs)', 'age', 0, 90, 10, this.filterAgex10);
		this.breakdown = this.breakdownOptions[0];
	}

	getOptionList(id : any, title : string, fieldName : string, filter : Filter[]) {
		let vm = this;
		this.breakdownOptions.push({id: id, name: title, field: fieldName, filters: []});

		vm.utilService.getDistinctValues(fieldName)
			.subscribe(
				(result) => {
					for (let value of result)
						if (value != null)
							filter.push({id: value, name: value.toString()});
				}
			);
	}

	getAgeBands(id : any, title : string, field : string, min : number, max : number, step : number, filter : Filter[]) {
		this.breakdownOptions.push({ id : id, name : title, field : field, filters : [] });

		let i = min;
		while (i <= max) {
			let band = '';

			if (i==min)
				band = '< '+min;
			else if (i + step > max)
				band = '> '+ i;
			else
				band = i + '-' + (i+step);

			filter.push({id : band, name: band});
			i+= step;
		}
	}

	refresh() {
		let vm = this;
		vm.utilService.getIncPrevResults(vm.breakdown.field, vm.genders, vm.ethnicity, vm.postcode, vm.lsoa, vm.msoa, vm.agex10)
			.subscribe(
				(results) => vm.drawGraph(results),
				(error) => console.log(error)
			);
	}

	drawGraph(results : any) {
		this.chart = this.getChartData(results);
	}

	//---------------------------

	getChartData(results: any) {
		if (results[0].length == 3)
			return this.getGroupedChartData(results);

		return this.getTotalChartData(results);
	}

	getTotalChartData(results : any) {
		let categories : string[] = [];
		let data : string[] = [];

		for (let row of results) {
			categories.push(row[0]);
			data.push(row[1]);
		}

		return new Chart()
			.setCategories(categories)
			.setColors(this.colors)
			.setHeight(this.height)
			.setLegend(this.legend)
			.setTitle(this.title)
			.addYAxis(this.title, false)
			.setSeries([
				new Series()
					.setName('Total')
					.setType('spline')
					.setData(data)
			]);
	}

	getGroupedChartData(results : any) {
		let currSeriesName : string = null;
		let series : Series = null;
		let seriesList : Series[] = [];
		let categories : string[] = [];

		for (let row of results) {
			let rowSeriesName = row[2] == null ? 'Unknown' : row[2].toString();
			if (currSeriesName != rowSeriesName) {
				currSeriesName = rowSeriesName;
				series = new Series()
					.setName(rowSeriesName)
					.setType('column')
					.setData([]);
				seriesList.push(series);
			}

			if(seriesList.length == 1)
				categories.push(row[0]);

			series.data.push(row[1]);
		}

		return new Chart()
			.setCategories(categories)
			.setColors(this.colors)
			.setHeight(this.height)
			.setLegend(this.legend)
			.setTitle(this.title)
			.addYAxis(this.title, false)
			.setSeries(seriesList);
	}

	//----------------------------

	export() {

		let rowData = [];

		rowData.push(this.chart.title);
		rowData = rowData.concat(this.chart.getRowData())

		let blob = new Blob([rowData.join('\n')], { type: 'text/plain' });
		window['saveAs'](blob, this.title + '.csv');
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
