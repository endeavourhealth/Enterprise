import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Chart} from "../charting/models/Chart";
import {UtilitiesService} from "./utilities.service";
import {Breakdown} from "./models/Breakdown";
import {Series} from "../charting/models/Series";

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
	private filter : any;
	private chart: Chart;
	private multiSelectSettings = {
		enableSearch: true,
		checkedStyle: 'fontawesome',
		buttonClasses: 'form-control text-left',
		dynamicTitleMaxItems: 3,
		displayAllSelectedText: true,
		showCheckAll: true,
		showUncheckAll: true,
		closeOnClickOutside: true
	};

	private colors = ['LightBlue', 'Plum', 'Yellow', 'LightSalmon'];						// Male, Female, Other, Total
	private height = 500;
	private legend = {align: 'right', layout: 'vertical', verticalAlign: 'middle', width: 100};

	constructor(protected $uibModalInstance : NgbActiveModal, protected utilService : UtilitiesService) {
	}

	ngOnInit(): void {
		this.getBreakdownOptions();
		this.refresh();
	}

	getBreakdownOptions() {
		this.breakdownOptions = [{ id : 0, name : 'None', field : null, filters : [] }];
		this.breakdownOptions.push(this.getOptionList(1, 'Gender', 'patient_gender_id'));
		this.breakdownOptions.push(this.getOptionList(2, 'Ethnicity', 'ethnic_code'));
		this.breakdownOptions.push(this.getOptionList(3, 'Postcode', 'postcode_prefix'));
		this.breakdownOptions.push(this.getOptionList(4, 'LSOA', 'lsoa_code'));
		this.breakdownOptions.push(this.getOptionList(5, 'MSOA', 'msoa_code'));
		this.breakdownOptions.push(this.getAgeBands(6,'Age band (5 yrs)', 'age', 0, 90, 5));
		this.breakdownOptions.push(this.getAgeBands(6,'Age band (10 yrs)', 'age', 0, 90, 10));
		this.breakdown = this.breakdownOptions[0];
	}

	getOptionList(id : any, title : string, fieldName : string) : Breakdown {
		let vm = this;
		let entry : Breakdown = { id : id, name : title, field : fieldName, filters : [] };

		vm.utilService.getDistinctValues(fieldName)
			.subscribe(
				(result) => {
					for (let filter of result)
						if (filter)
							entry.filters.push({id : filter, name: filter.toString()});
				}
			);

		return entry;
	}

	getAgeBands(id : any, title : string, field : string, min : number, max : number, step : number) : Breakdown {
		let entry : Breakdown = { id : id, name : title, field : field, filters : [] };

		let i = min;
		while (i <= max) {
			let band = '';

			if (i==min)
				band = '< '+min;
			else if (i + step > max)
				band = '> '+ i;
			else
				band = i + '-' + (i+step);

			entry.filters.push({id : band, name: band});
			i+= step;
		}

		return entry;
	}

	refresh() {
		let vm = this;
		vm.utilService.getIncPrevResults(vm.breakdown.field, vm.filter)
			.subscribe(
				(results) => vm.drawGraph(results),
				(error) => console.log(error)
			);
	}

	drawGraph(results : any) {
		let chartData = this.getChartData(results);
		this.chart = this.createIncidenceChart(chartData);
	}

	setBreakdown() {
		this.filter = null;
	}


	//---------------------------

	getChartData(results: any) {

		let chartData: any = {
			categories: [],
			incidence_total: [],
			incidence_male: [],
			incidence_female: [],
			incidence_other: [],

			prevalence_total: [],
			prevalence_male: [],
			prevalence_female: [],
			prevalence_other: [],

			population_total: [],
			population_male: [],
			population_female: [],
			population_other: []
		};

		// Determine if year or month
		let monthly : boolean = false;
		if (results.length > 1)
			monthly = results[0][0].substring(5, 7) != results[1][0].substring(5, 7);


		for (let row of results) {
			chartData.categories.push(row[0].substring(0, monthly ? 7 : 4));
			chartData.incidence_male.push(row[1]);
			chartData.incidence_female.push(row[2]);
			chartData.incidence_other.push(row[3]);
			chartData.incidence_total.push(row[1] + row[2] + row[3]);

			chartData.prevalence_male.push(this.calcPercentage(row[4], row[7]));
			chartData.prevalence_female.push(this.calcPercentage(row[5], row[8]));
			chartData.prevalence_other.push(this.calcPercentage(row[6], row[9]));
			chartData.prevalence_total.push(this.calcPercentage(row[4]+row[5]+row[6], row[7]+row[8]+row[9]));

			chartData.population_male.push(row[7]);
			chartData.population_female.push(row[8]);
			chartData.population_other.push(row[9]);
			chartData.population_total.push(row[7]+row[8]+row[9]);
		}

		return chartData;
	}

	private createIncidenceChart(chartData: any): Chart {
		return this.createChart(
			chartData.categories,
			'Incidence',
			chartData.incidence_male,
			chartData.incidence_female,
			chartData.incidence_other,
			chartData.incidence_total
		);
	}

	private createChart(categories: string[], title: string, male: number[], female: number[], other: number[], total: number[]) {
		return new Chart()
			.setCategories(categories)
			.setColors(this.colors)
			.setHeight(this.height)
			.setLegend(this.legend)
			.setTitle(title)
			.addYAxis(title, false)
			.addYAxis('Total', true)
			.setSeries([
				new Series()
					.setType('column')
					.setName('Male')
					.setData(male),
				new Series()
					.setType('column')
					.setName('Female')
					.setData(female),
				new Series()
					.setType('column')
					.setName('Other')
					.setData(other),
				new Series()
					.setType('spline')
					.setName('Total')
					.setData(total)
					.setyAxis(1)
			]);
	}

	calcPercentage(incidence, population: number): number {
		if (population == 0)
			return 0;

		// Leading '+' causes result to be number rather than string
		return +((100 * incidence) / population).toFixed(1);
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
