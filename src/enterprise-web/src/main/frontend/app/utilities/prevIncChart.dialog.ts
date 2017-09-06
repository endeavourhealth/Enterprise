import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Chart} from "../charting/models/Chart";
import {UtilitiesService} from "./utilities.service";
import {MinLengthValidator} from "@angular/forms";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./prevIncChart-dialog.html')
})
export class PrevIncChartDialog implements OnInit {

	public static open(modalService: NgbModal, title : string, chartData: Chart) {
		const modalRef = modalService.open(PrevIncChartDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.title = title;
		modalRef.componentInstance.chart = chartData;

		return modalRef;
	}

	@Input() title;
	@Input() chart: Chart;

	private breakdown : any;
	private breakdownOptions : any[] = [
		{id: 0, name: 'None', filters: []},
		{id: 1, name: 'Gender', filters: [
			{id: 'M', name: 'Male'},
			{id: 'F', name: 'Female'}
			]},
		{id: 2, name: 'Ethnicity', filters: [
			{id: 'A', name: 'Caucasian'},
			{id: 'B', name: 'Afro-carribean'},
			{id: 'C', name: 'Asian'}
		]},
		{id: 3, name: 'Age (/5)', filters: ['0-5','5-10','10-15']},
		{id: 4, name: 'Age (/10)', filters: ['0-10','10-20','20-30']},
		{id: 5, name: 'Postcode', filters: ['LS1','LS2','LS3','LS4']},
		{id: 6, name: 'LSOA', filters: ['']},
		{id: 7, name: 'MSOA', filters: ['']},
		];
	private filter : any;
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

	constructor(protected $uibModalInstance : NgbActiveModal, protected utilService : UtilitiesService) {
		this.breakdown = this.breakdownOptions[0];
	}

	ngOnInit(): void {
		this.breakdownOptions = [];
		this.breakdownOptions.push(this.getOptionList(1, 'Gender', 'patient_gender_id'));
		this.breakdownOptions.push(this.getOptionList(2, 'Ethnicity', 'ethnic_code'));
		this.breakdownOptions.push(this.getOptionList(3, 'Postcode', 'postcode_prefix'));
		this.breakdownOptions.push(this.getOptionList(4, 'LSOA', 'lsoa_code'));
		this.breakdownOptions.push(this.getOptionList(5, 'MSOA', 'msoa_code'));
		this.breakdownOptions.push(this.getAgeBands(6,'Age band (5 yrs)', 0, 90, 5));
		this.breakdownOptions.push(this.getAgeBands(6,'Age band (10 yrs)', 0, 90, 10));
	}

	getOptionList(id : any, title : string, fieldName : string) {
		let vm = this;
		let entry = { id : id, name : title, filters : [] };

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

	getAgeBands(id : any, title : string, min : number, max : number, step : number) {
		let entry = { id : id, name : title, filters : [] };

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

	onChange() {
		console.log(this.filter);
	}

	setBreakdown() {
		this.filter = null;
		console.log('filter cleared');
	}

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
