import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {PrevInc} from "./models/PrevInc";
import {Organisation} from "../report/models/Organisation";
import {LoggerService, MessageBoxDialog} from "eds-common-js";
import {CohortService} from "../cohort/cohort.service";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./prevInc.html')
})
export class PrevIncDialog implements OnInit {

	public static open(modalService: NgbModal, prevInc: PrevInc) {
		const modalRef = modalService.open(PrevIncDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.resultData = prevInc;

		return modalRef;
	}

	@Input() resultData;

	organisation: Organisation = <any>[];
	population: string = "";

	orgTT: string = "Please select one or more organisations to include. The query will run against every organisation selected. To select multiple organisation please use Shift and Click.";
	ppTT: string = "Please select a patient population as the denominator.";

	populations = [
		{id: -1, type: ''},
		{id: 0, type: 'Currently registered'},
		{id: 1, type: 'All patients'},
	];

	codeset = [
		{id: -1, name: ''},
		{id: 0, name: 'Diabetes Code Set'},
		{id: 1, name: 'Asthma Code Set'}
	];

	organisations = <any>[];
	periods = ['','MONTHS','YEARS'];

	constructor(protected cohortService: CohortService,
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

	setSelectedPopulation(selectElement) {
		var vm = this;
		vm.resultData.population = "";
		if (selectElement.selectedOptions.length > 0)
			vm.resultData.population = selectElement.selectedOptions[0].value;
	}

	run() {
		var vm = this;
		this.ok();
	}

	ok() {
		this.$uibModalInstance.close(this.resultData);
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
