import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {PrevInc} from "./models/PrevInc";
import {Organisation} from "../report/models/Organisation";
import {LoggerService, MessageBoxDialog} from "eds-common-js";
import {CohortService} from "../cohort/cohort.service";
import {UtilitiesService} from "./utilities.service";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./prevInc.html')
})
export class PrevIncDialog implements OnInit {

	codeSets:FolderItem[];

	public static open(modalService: NgbModal, prevInc: PrevInc) {
		const modalRef = modalService.open(PrevIncDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.resultData = prevInc;

		return modalRef;
	}

	@Input() resultData;

	organisation: Organisation = <any>[];
	population: string = "0";
	codeSet: string = "";
	timePeriodNo: string = "10";
	timePeriod: string = "YEARS";
	title: string = "Incidence and Prevalence";

	orgTT: string = "Please select one or more organisations to include. The query will run against every organisation selected. To select multiple organisation please use Shift and Click.";
	ppTT: string = "Please select a patient population as the denominator.";
	ppTM: string = "Please select the time period to trend.";

	populations = [
		{id: -1, type: ''},
		{id: 0, type: 'Currently registered'},
		{id: 1, type: 'All patients'},
	];

	organisations = <any>[];
	periods = ['','MONTHS','YEARS'];

	constructor(protected cohortService: CohortService,
							private utilitiesService:UtilitiesService,
							private $modal: NgbModal,
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

		vm.getCodeSets();
	}

	getCodeSets() {
		var vm = this;

		vm.codeSets = null;
		vm.utilitiesService.getCodeSets()
			.subscribe(
				(data:FolderItem[]) => vm.codeSets = data
			);
	}

	setSelectedOrganisations(selectElement) {
		var vm = this;
		vm.resultData.organisation = <any>[];
		for (let optionElement of selectElement.selectedOptions) {
			let org = {
				id: optionElement.value,
				name: optionElement.text
			};
			vm.resultData.organisation.push(org);
		}
	}

	run() {
		var vm = this;
		vm.resultData.population = vm.population;
		vm.resultData.codeSet = vm.codeSet;
		vm.resultData.timePeriodNo = vm.timePeriodNo;
		vm.resultData.timePeriod = vm.timePeriod;
		vm.resultData.title = vm.title;

		this.ok();
	}

	ok() {
		this.$uibModalInstance.close(this.resultData);
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
