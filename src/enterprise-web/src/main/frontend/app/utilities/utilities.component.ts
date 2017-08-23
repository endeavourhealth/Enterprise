import {Component} from "@angular/core";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {Transition} from "ui-router-ng2";
import {StateService} from "ui-router-ng2";
import {UtilitiesService} from "./utilities.service";
import {LoggerService} from "eds-common-js";
import {PrevIncDialog} from "./prevInc.dialog";
import {PrevInc} from "./models/PrevInc";
import {Chart} from "../charting/models/Chart";
import {Series} from "../charting/models/Series";
import {ChartDialog} from "../charting/chart.dialog";
import {TableDialog} from "../charting/table.dialog";
import {DualDialog} from "../charting/dual.dialog";

@Component({
	template : require('./utilities.html')
})
export class UtilitiesComponent {

	incPrevRunning : boolean = false;

	constructor(private utilitiesService:UtilitiesService,
							private transition: Transition,
							private logger:LoggerService,
							private $modal: NgbModal,
							private $state : StateService) {

		this.performAction(transition.params()['utilId']);

	}

	private performAction(utilId: string) {
		switch (utilId) {
			case "prev-inc":
				this.pi();
				break;
			default:
		}
	}


	pi () {
		let vm = this;
		let prevInc: PrevInc = {
			organisation: [],
			population: "0",
			codeSet: "0",
			timePeriodNo: "10",
			timePeriod: "YEARS"
		};
		PrevIncDialog.open(vm.$modal, prevInc).result.then(
			(result) => {
				console.log(result);
				if (result)
					vm.runReport(result);
				else
					console.log('Cancelled');
			},
			(error) => vm.logger.error("Error running utility", error)
		);
	}

	runReport(options: PrevInc) {
		let vm = this;
		vm.incPrevRunning = true;
		vm.utilitiesService.runPrevIncReport(options)
			.subscribe(
				(result) => {
					vm.incPrevRunning = false;
					console.log('report complete')
				},
				(data) => vm.logger.error('Error loading', data, 'Error')
			);
	}

	chart () {
		let chartData = new Chart()
			.setTitle('Prevalence and Incidence')
			.setCategories(['2001', '2002', '2003', '2004', '2005'])
			.setSeries([
				new Series()
					.setType('column')
					.setName('Male')
					.setData([3, 2, 1, 3, 4]),
				new Series()
					.setType('column')
					.setName('Female')
					.setData([2, 3, 5, 7, 6]),
				new Series()
					.setType('spline')
					.setName('T')
					.setData([3, 2.67, 3, 6.33, 3.33])
			]);

		ChartDialog.open(this.$modal, 'Prevalence and Incidence', chartData);
	}

	table() {
		let tableData = new Chart()
			.setTitle('Prevalence and Incidence')
			.setCategories(['2001', '2002', '2003', '2004', '2005'])
			.setSeries([
				new Series()
					.setType('column')
					.setName('Male')
					.setData([3, 2, 1, 3, 4]),
				new Series()
					.setType('column')
					.setName('Female')
					.setData([2, 3, 5, 7, 6]),
				new Series()
					.setType('spline')
					.setName('T')
					.setData([3, 2.67, 3, 6.33, 3.33])
			]);

		TableDialog.open(this.$modal, 'Prevalence and Incidence', tableData);
	}

	showResults() {
		let vm = this;
		vm.utilitiesService.getIncPrevResults()
			.subscribe(
				(result) => vm.graphIncPrev(result),
				(error) => console.error(error)
			);
	}

	graphIncPrev(results : any) {
		let categories : string[] = [];
		let incidence_total : number[] = [];
		let incidence_male : number[] = [];
		let incidence_female : number[] = [];

		let spacer : number[] = [];

		let prevalence_total : number[] = [];
		let prevalence_male : number[] = [];
		let prevalence_female : number[] = [];
		let prevalence_other : number[] = [];


		for(let row of results) {
			categories.push(row[1].substring(0,4));
			incidence_total.push(row[3]);
			incidence_male.push(row[4]);
			incidence_female.push(row[5]);

			spacer.push(null);

			prevalence_total.push(this.calcPercentage(row[11],row[7]));
			prevalence_male.push(this.calcPercentage(row[12], row[8]));
			prevalence_female.push(this.calcPercentage(row[13], row[9]));
			prevalence_other.push(this.calcPercentage(row[14], row[10]));
		}

		let chartData = new Chart()
			.setCategories(categories)
			.addYAxis('Incidence', false)
			.addYAxis('Prevalence (%)', true)
			.setSeries([
				new Series()
					.setType('column')
					.setName('Male (I)')
					.setData(incidence_male),
				new Series()
					.setType('column')
					.setName('Female (I)')
					.setData(incidence_female),
				new Series()
					.setType('spline')
					.setName('Total (I)')
					.setData(incidence_total),
				new Series()
					.setType('column')
					.setName(null)
					.setData(spacer),
				new Series()
					.setType('column')
					.setName('Male (P)')
					.setData(prevalence_male)
					.setyAxis(1),
				new Series()
					.setType('column')
					.setName('Female (P)')
					.setData(prevalence_female)
					.setyAxis(1),
				new Series()
					.setType('column')
					.setName('Other (P)')
					.setData(prevalence_other)
					.setyAxis(1),
				new Series()
					.setType('spline')
					.setName('Total (P)')
					.setData(prevalence_total)
					.setyAxis(1)
			]);

		DualDialog.open(this.$modal, 'Prevalence and Incidence', chartData);
	}

	calcPercentage(incidence, population : number) : number {
		if (population == 0)
			return 0;

		// Leading '+' causes result to be number rather than string
		return +((100 * incidence) / population).toFixed(1);
	}
}

