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
import {StackDialog} from "../charting/stack.dialog";

@Component({
	template : require('./utilities.html')
})
export class UtilitiesComponent {

	private colors = ['LightBlue', 'Plum', 'Yellow', 'LightSalmon'];						// Male, Female, Other, Total
	private height = 210;
	private legend = {align: 'right', layout: 'vertical', verticalAlign: 'middle', width: 100};

	incPrevRunning: boolean = false;

	colName: string;
	distinctValues: string[];

	constructor(private utilitiesService: UtilitiesService,
							private transition: Transition,
							private logger: LoggerService,
							private $modal: NgbModal,
							private $state: StateService) {

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


	pi() {
		let vm = this;
		let prevInc: PrevInc = {
			organisation: [],
			population: "0",
			codeSet: "0",
			timePeriodNo: "10",
			timePeriod: "YEARS",
			title: 'Incidence and Prevalence',
			diseaseCategory: "0",
			postCodePrefix: "",
			lsoaCode: [],
			msoaCode: [],
			sex: "-1",
			ethnicity: [],
			orgType: "",
			ageFrom: "",
			ageTo: ""
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

	showResults() {
		let vm = this;
		vm.utilitiesService.getIncPrevResults()
			.subscribe(
				(result) => vm.graphIncPrev(result),
				(error) => console.error(error)
			);
	}

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

	graphIncPrev(results: any) {

		let chartData = this.getChartData(results);


		let charts = [
			this.createIncidenceChart(chartData),
			this.createPrevalenceChart(chartData),
			this.createPopulationChart(chartData)
		];

		StackDialog.open(this.$modal, results[0][13], charts);
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

	private createPrevalenceChart(chartData: any) {
		return this.createChart(
			chartData.categories,
			'Prevalence (%)',
			chartData.prevalence_male,
			chartData.prevalence_female,
			chartData.prevalence_other,
			chartData.prevalence_total
		);
	}

	private createPopulationChart(chartData: any) {
		return this.createChart(
			chartData.categories,
			'Population',
			chartData.population_male,
			chartData.population_female,
			chartData.population_other,
			chartData.population_total
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

    getDistinctValues() {
        let vm = this;
        vm.utilitiesService.getDistinctValues(vm.colName)
            .subscribe(
                (result) => vm.distinctValues = result,
                (error) => console.error(error)
            );
	}
}

