import {Component, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Chart} from "../../charting/models/Chart";
import {Series} from "../../charting/models/Series";
import {IndDashService} from "./indDash.service";
import {QuerySelection} from '../../query/models/QuerySelection';
import {QueryPickerDialog} from '../../query/queryPicker.dialog';
import {Logger} from 'angular2-logger/core';
import moment = require('moment');

@Component({
	selector: 'ngbd-modal-content',
	template: require('./indDashChart-dialog.html')
})
export class IndDashChartDialog implements OnInit {

	public static open(modalService: NgbModal) {
		const modalRef = modalService.open(IndDashChartDialog, { backdrop : "static", size : "lg"});
		return modalRef;
	}

	private cohort: QuerySelection;
	private orgMap: Map<number, any> = new Map();
	private orgs: any[];
	private selectedOrgs: any[] = [];
	private graphData: Chart = null;
	private loading: boolean;
	private height = 500;
	private legend = {align: 'right', layout: 'vertical', verticalAlign: 'middle', width: 100};


	constructor(protected $uibModalInstance : NgbActiveModal,
							private $modal: NgbModal,
							private $log: Logger,
							protected indDashService : IndDashService) {
	}

	ngOnInit(): void {
	}

	pickCohort() {
		const vm = this;
		QueryPickerDialog.open(this.$modal, null)
			.result.then(
			(result: QuerySelection) => vm.selectCohort(result),
			(cancel) => console.log(cancel)
		);
	}

	selectCohort(cohort: QuerySelection) {
		this.cohort = cohort;
		this.loadCohortRunOrganisations();
	}

	loadCohortRunOrganisations() {
		const vm = this;
		vm.selectedOrgs = [];
		vm.indDashService.getCohortRunOrganisations(vm.cohort.id)
			.subscribe(
				(result) => vm.populateCohortRunOrganisations(result),
				(error) => vm.$log.error(error)
			);
	}

	populateCohortRunOrganisations(orgs: any[]) {
		this.orgs = orgs;
		this.orgMap.clear();
		for (const org of orgs) {
			this.orgMap.set(org.id, org);
		}
	}

	loadGraph() {
		const vm = this;
		vm.loading = true;
		vm.indDashService.getGraphData(vm.cohort.id, vm.selectedOrgs)
			.subscribe(
				(result) => vm.displayGraph(result),
				(error) => vm.$log.error(error)
			);
	}

	displayGraph(data: any) {
		const dates: string[] = [];
		const series: Map<number, Series> = new Map();

		let lastDate: string = '';
		for (const row of data) {
			const thisDate = moment(row[0]).format('DD-MMM-YYYY');
			if (lastDate !== thisDate) {
				lastDate = thisDate;
				dates.push(thisDate)
			}

			let orgSeries = series.get(row[2]);
			if (!orgSeries) {
				orgSeries = new Series()
					.setName(this.orgMap.get(row[2]).name);
				series.set(row[1], orgSeries);
			}

			orgSeries.addData({ name: thisDate, y: row[1]});
		}

		this.graphData = new Chart()
			.setCategories(dates)
			.setHeight(this.height)
			.setLegend(this.legend)
			.addYAxis('% of cohort', false)
			.setSeries(Array.from(series.values()));

		this.loading = false;
	}

	export() {

		let rowData = [];

		let blob = new Blob([rowData.join('\n')], { type: 'text/plain' });
		window['saveAs'](blob, 'CohortTrend.csv');
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
