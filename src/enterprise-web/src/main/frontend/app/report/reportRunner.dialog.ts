import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {ReportRun} from "./models/ReportRun";
import {Organisation} from "./models/Organisation";
import {LoggerService, MessageBoxDialog} from "eds-common-js";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {CohortService} from "../cohort/cohort.service";
import {QueryPickerDialog} from "../query/queryPicker.dialog";
import {QuerySelection} from "../query/models/QuerySelection";
import {OrganisationGroup} from "../organisationGroup/models/OrganisationGroup";
import {OrgGroupPickerComponent} from "../organisationGroup/orgGroupPicker.component";
import {OrganisationGroupService} from "../organisationGroup/organisationGroup.service";

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

	population: string = "";
	baselineDate: string = "";
	baselineCohort : QuerySelection;
	queryItemUuid: string = "";
	scheduled: boolean=false;
	scheduleDate: string = "";
	scheduleTime:string = "";

	ppTT: string = "Please select a patient population as the denominator.";
	rdTT: string = "Please specify a baseline date. i.e. patients registered on or before that date.";
	srTT: string = "Please specify an execution date/time.";
	bcTT: string = "[Optional] restrict patient population by a cohort definition.";

	populations = [
		{id: 0, type: 'Currently registered'},
		{id: 1, type: 'All patients'},
	];

	selectedGroupId: number = 1;
	orgGroups: OrganisationGroup[] = [];

	constructor(protected cohortService: CohortService,
							private orgGroupService: OrganisationGroupService,
							private $modal: NgbModal,
							protected $uibModalInstance : NgbActiveModal,
							private logger : LoggerService) {
	}

	ngOnInit(): void {
		var vm = this;

		vm.resultData.population = 0;
		vm.baselineDate = this.formatDate(new Date());
		console.log(vm.baselineDate);

		vm.getOrganisationGroups();
	}

	getOrganisationGroups() {
		var vm = this;
		vm.orgGroups = [];
		vm.orgGroupService.getOrganisationGroups()
			.subscribe(
				(result) => {
					for (let value of result) {
						if (value != null) {
							vm.orgGroups.push({id: value[0], name: value[1]});
						}
					}
				});
	}

	orgManager() {
		var vm = this;
		OrgGroupPickerComponent.open(this.$modal, vm.selectedGroupId ).result.then(
			(result) => {
				if (result) {
					vm.selectedGroupId = result;
					vm.getOrganisationGroups();
					console.log(vm.selectedGroupId);
				}
				else
					console.log('Cancelled');
			},
			(error) => vm.logger.error("Error running utility", error)
		);
	}

	formatDate(date) {
		var d = new Date(date),
			month = '' + (d.getMonth() + 1),
			day = '' + d.getDate(),
			year = d.getFullYear();

		if (month.length < 2) month = '0' + month;
		if (day.length < 2) day = '0' + day;

		return [year, month, day].join('-');
	}

	setSelectedPopulation(selectElement) {
		var vm = this;
		vm.resultData.population = "";
		if (selectElement.selectedOptions.length > 0)
			vm.resultData.population = selectElement.selectedOptions[0].value;
	}

	pickBaselineCohort() {
		let vm = this;
		QueryPickerDialog.open(this.$modal, null)
			.result.then((resultData: QuerySelection) => vm.baselineCohort = resultData);
	}

	run() {
		var vm = this;
		vm.resultData.baselineDate = vm.baselineDate;
		vm.resultData.organisationGroup = vm.selectedGroupId;
		vm.resultData.scheduled = vm.scheduled;
		vm.resultData.scheduleDateTime = new Date(vm.scheduleDate + " " + vm.scheduleTime);
		if (vm.baselineCohort)
			vm.resultData.baselineCohortId = vm.baselineCohort.id;

		this.ok();
	}

	getRunCaption() {
		if (this.scheduled)
			return 'Schedule';
		return 'Run now';
	}

	confirmReportRunWithoutRestrictiveCohort() {
		let vm = this;
		MessageBoxDialog.open(vm.$modal, "Confirm report", "Not specifying a restrictive cohort may result in long run times.  Continue?", "Yes", "No")
			.result.then(() => vm.$uibModalInstance.close(vm.resultData));
	}

	ok() {
		if (this.baselineCohort)
			this.$uibModalInstance.close(this.resultData);
		else
			this.confirmReportRunWithoutRestrictiveCohort();
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
