import {Component} from "@angular/core";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {Transition} from "ui-router-ng2";
import {StateService} from "ui-router-ng2";
import {LoggerService} from "eds-common-js";
import {PrevIncDialog} from "./prevInc/prevInc.dialog";
import {HealthCareActivityDialog} from "./healthCareActivity/healthCareActivity.dialog";
import {PrevInc} from "./models/PrevInc";
import {HealthCareActivity} from "./models/HealthCareActivity";
import {PrevIncChartDialog} from "./prevInc/prevIncChart.dialog";
import {IndDashChartDialog} from "./indDash/indDashChart.dialog";
import {PrevIncService} from "./prevInc/prevInc.service";
import {HealthCareActivityChart} from "./healthCareActivity/healthCareActivityChart.dialog";
import {HealthCareActivityService} from "./healthCareActivity/healthCareActivity.service";
import {CohortService} from "../cohort/cohort.service";
import {InputBoxDialog} from 'eds-common-js';
import {MessageBoxDialog} from 'eds-common-js';

@Component({
	template : require('./utilities.html')
})
export class UtilitiesComponent {

	incPrevRunning: boolean = false;
	hcaRunning: boolean = false;

	constructor(private prevIncService: PrevIncService,
							private healthcareActivityService: HealthCareActivityService,
							private transition: Transition,
							private logger: LoggerService,
							private $modal: NgbModal,
							private $state: StateService,
							protected cohortService: CohortService) {

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
			organisationGroup: 0,
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
			ageTo: "",
			dateType: "absolute"
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
		vm.prevIncService.runPrevIncReport(options)
			.subscribe(
				(result) => {
					vm.incPrevRunning = false;
					console.log('report complete')
				},
				(data) => vm.logger.error('Error loading', data, 'Error')
			);
	}

    runHCAReport(options: HealthCareActivity) {
        let vm = this;
        vm.hcaRunning = true;
        vm.healthcareActivityService.runHealthCareActivityReport(options)
            .subscribe(
                (result) => {
                    vm.hcaRunning = false;
                    console.log('report complete')
                },
                (data) => vm.logger.error('Error loading', data, 'Error')
            );
    }

	showResultsPI() {
		PrevIncChartDialog.open(this.$modal, 'Results');
	}

	indDash() {
		IndDashChartDialog.open(this.$modal);
	}

	frailty() {
		let vm = this;

		InputBoxDialog.open(this.$modal, 'NHS111 Frailty Checker', 'Patient Pseudo Id', '0')
			.result.then(
			(result) => this.checkFrailty(result)
		);


	}

	checkFrailty(pseudoId: string) {
		let vm = this;
		vm.cohortService.getFrailty(pseudoId)
			.subscribe(
				(data) => {
					var message = "Patient is not frail";
					if (data)
						message = "Patient is frail";

					MessageBoxDialog.open(this.$modal, 'NHS111 Frailty Checker', message, 'Ok', 'Cancel');
					console.log(data)
				});
	}

	hca() {
		let vm = this;
		let healthCareActivity: HealthCareActivity = {
			organisationGroup: 0,
            population: "",
            orgType: "",
			timePeriodNo: "10",
			timePeriod: "YEARS",
			title: 'Health Care Activity',
			postCodePrefix: "",
			lsoaCode: [],
			msoaCode: [],
			sex: "-1",
			ethnicity: [],
			ageFrom: "",
			ageTo: "",
			dateType: "absolute",
			serviceGroupId: 0,
			encounterType: []
		};
		HealthCareActivityDialog.open(vm.$modal, healthCareActivity).result.then(
			(result) => {
				console.log(result);
				if (result)
					vm.runHCAReport(result);
				else
					console.log('Cancelled');
			},
			(error) => vm.logger.error("Error running utility", error)
		);
	}

	showResultsHCA() {
		HealthCareActivityChart.open(this.$modal, 'Results');
	}
}

