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

@Component({
	template : require('./utilities.html')
})
export class UtilitiesComponent {

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
				vm.test(result);
			},
			(error) => vm.logger.error("Error running utility", error)
		);
	}

	test(options: PrevInc) {
		let vm = this;
		vm.utilitiesService.runDiabetesReport(options)
			.subscribe(
				(result) => {
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

		ChartDialog.open(this.$modal, chartData);
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

		TableDialog.open(this.$modal, tableData);
	}
}

