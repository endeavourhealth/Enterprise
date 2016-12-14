import {OrganisationSet} from "./models/OrganisationSet";
import {OrganisationSetMember} from "./models/OrganisationSetMember";
import {DialogBase} from "../dialogs/dialog.base";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Input, Component, OnInit} from "@angular/core";
import {LoggerService} from "../common/logger.service";
import {OrganisationSetService} from "./organisationSet.service";
import {MessageBoxDialog} from "../dialogs/messageBox/messageBox.dialog";
import {InputBoxDialog} from "../dialogs/inputBox/inputBox.dialog";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./organisationPicker.html')
})
export class OrganisationSetPickerDialog extends DialogBase implements OnInit {
	@Input() organisationList : OrganisationSetMember[];
	@Input() organisationSet : OrganisationSet;

	public static open(modalService: NgbModal, organisationList : OrganisationSetMember[], organisationSet : OrganisationSet) {
		const modalRef = modalService.open(OrganisationSetPickerDialog, { backdrop : "static"});
		modalRef.componentInstance.organisationList = organisationList;
		modalRef.componentInstance.organisationSet = organisationSet;

		return modalRef;
	}

	organisationSetList : OrganisationSet[];
	searchCriteria : string;
	searchResults : OrganisationSetMember[];
	editMode : boolean;

	constructor(protected $uibModalInstance : NgbActiveModal,
							private $modal : NgbModal,
							private log : LoggerService,
							private organisationService : OrganisationSetService) {
		super($uibModalInstance);
	}

	ngOnInit() {
		this.loadOrganisationSets();

		if (this.organisationSet) {
			this.editMode = true;
			this.selectSet(this.organisationSet);
		} else {
			this.editMode = false;
			this.resultData = {
				uuid: null,
				name: '<New Organisation Set>',
				organisations: this.organisationList
			};
		}
	}

	loadOrganisationSets() {
		let vm = this;
		vm.organisationService.getOrganisationSets()
			.subscribe((result) => vm.organisationSetList = result);
	}

	selectSet(organisationSet : OrganisationSet) {
		let vm = this;
		if (organisationSet === null) {
			// Clear uuid and name (but not organisation list in case of "Save As")
			vm.resultData.uuid = null;
			vm.resultData.name = '<New Organisation Set>';
		} else {
			// Create COPY of selected set (in case of "Save As")
			vm.resultData = {
				uuid : organisationSet.uuid,
				name : organisationSet.name
			};
			vm.organisationService.getOrganisationSetMembers(organisationSet.uuid)
				.subscribe((result) => vm.resultData.organisations = result);
		}
	}

	search() {
		let vm = this;
		vm.organisationService.searchOrganisations(vm.searchCriteria)
			.subscribe((result) => vm.searchResults = result);
	}

	addOrganisationToSelection(organisation : OrganisationSetMember) {
		if (this.resultData.organisations.every((item : OrganisationSetMember) => item.odsCode !== organisation.odsCode)) {
			this.resultData.organisations.push(organisation);
		}
	}

	removeOrganisationFromSelection(organisation : OrganisationSetMember) {
		let index = this.resultData.organisations.indexOf(organisation);
		this.resultData.organisations.splice(index, 1);
	}

	removeAll() {
		this.resultData.organisations = [];
	}

	saveSet() {
		let vm = this;
		if (vm.resultData.uuid === null) {
			InputBoxDialog.open(vm.$modal, 'Save Organisation Set', 'Enter Set Name', 'New Organisation Set')
				.result.then(function(result) {
					vm.resultData.name = result;
					vm.save();
			});
		} else {
			MessageBoxDialog.open(vm.$modal, 'Save Organisation Set',
				'You are about to update an existing set, are you sure you want to continue?', 'Yes', 'No')
				.result.then(function() {
					vm.save();
			});
		}
	}

	save() {
		let vm = this;
		vm.organisationService.saveOrganisationSet(vm.resultData)
			.subscribe(
				(result) => {
					vm.log.success('Organisation Set Saved', result, 'Saved');
					if (vm.resultData.uuid === null) {
						vm.resultData.uuid = result.uuid;
						vm.organisationSetList.push(vm.resultData);
					}
				});
	}
}
