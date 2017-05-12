import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {ReportRun} from "./models/ReportRun";
import {Organisation} from "./models/Organisation";
import {LoggerService} from "eds-common-js";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {CohortService} from "../cohort/cohort.service";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./reportRunner.html')
})
export class ReportRunnerDialog implements OnInit {

	public static open(modalService: NgbModal, reportRun: ReportRun, item: FolderItem) {
		const modalRef = modalService.open(ReportRunnerDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.resultData = reportRun;
		modalRef.componentInstance.item = item;

		return modalRef;
	}

	@Input() resultData;
	@Input() item = FolderItem;

	organisation: Organisation = <any>[];
	population: string = "";
	baselineDate: string = "";
	queryItemUuid: string = "";
	scheduled: boolean=false;
	scheduleDate: string = "";
	scheduleTime:string = "";

	orgTT: string = "Please select one or more organisations to include. The query will run against every organisation selected. To select multiple organisation please use Shift and Click.";
	ppTT: string = "Please select a patient population as the denominator.";
	rdTT: string = "Please specify a baseline date. i.e. patients registered on or before that date.";
	srTT: string = "Please specify an execution date/time.";

	populations = [
		{id: -1, type: ''},
		{id: 0, type: 'Currently registered'},
		{id: 1, type: 'All patients'}
	];

	organisations = <any>[];

	constructor(protected cohortService: CohortService,
							protected $uibModalInstance : NgbActiveModal,
							private logger : LoggerService) {

	}

	ngOnInit(): void {
		var vm = this;

		vm.cohortService.getOrganisations()
			.subscribe(
				(data) => {
					vm.organisations = data;
				});
	}

	setSelectedOrganisations(selectElement) {
		var vm = this;
		vm.resultData.organisation = <any>[];
		for (var i = 0; i < selectElement.options.length; i++) {
			var optionElement = selectElement.options[i];
			if (optionElement.selected) {
				let org = {
					id: optionElement.value,
					name: optionElement.text
				};
				vm.resultData.organisation.push(org);
			}
		}
	}

	setSelectedPopulation(selectElement) {
		var vm = this;
		vm.resultData.population = "";
		for (var i = 0; i < selectElement.options.length; i++) {
			var optionElement = selectElement.options[i];
			if (optionElement.selected) {
				vm.resultData.population = optionElement.value;
			}
		}
	}

	save() {
		var vm = this;
		vm.resultData.baselineDate = vm.baselineDate;
		vm.resultData.scheduled = vm.scheduled;
		vm.resultData.scheduleDateTime = new Date(vm.scheduleDate + " " + vm.scheduleTime);

		vm.logger.info("Schedule " + vm.resultData.scheduleDateTime);

		this.ok();
	}

	ok() {
		this.$uibModalInstance.close(this.resultData);
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
