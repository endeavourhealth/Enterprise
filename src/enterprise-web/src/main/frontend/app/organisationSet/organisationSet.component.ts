import {Component} from "@angular/core";
import {OrganisationSet} from "./models/OrganisationSet";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {LoggerService} from "../common/logger.service";
import {OrganisationSetMember} from "./models/OrganisationSetMember";
import {OrganisationSetService} from "./organisationSet.service";
import {MessageBoxDialog} from "../dialogs/messageBox/messageBox.dialog";
import {OrganisationSetPickerDialog} from "./organisationPicker.dialog";

@Component({
	template : require('./organisationSet.html')
})
export class OrganisationSetComponent {
	organisationSets: OrganisationSet[];
	selectedOrganisationSet: OrganisationSet;

	static $inject = ['$uibModal', 'OrganisationService', 'LoggerService'];

	constructor(private $modal: NgbModal,
							private organisationService: OrganisationSetService,
							private log: LoggerService) {
		this.getRootFolders();
	}

	getOrganisationSets() {
		// TODO : Ordering
		return this.organisationSets;
	}

	getRootFolders() {
		let vm = this;
		vm.organisationService.getOrganisationSets()
			.subscribe(
				(result) => vm.organisationSets = result,
				(error) => vm.log.error('Failed to load sets', error, 'Load sets')
			);
	}

	selectOrganisationSet(item: OrganisationSet) {
		let vm = this;

		vm.selectedOrganisationSet = item;

		// Load members if necessary
		if (!item.organisations || item.organisations.length === 0) {
			vm.organisationService.getOrganisationSetMembers(item.uuid)
				.subscribe(
					(result: OrganisationSetMember[]) => vm.selectedOrganisationSet.organisations = result
				);
		}
	}

	showOrganisationPicker() {
		let vm = this;
		OrganisationSetPickerDialog.open(vm.$modal, null, vm.selectedOrganisationSet)
			.result.then(function (organisationSet: OrganisationSet) {
			vm.organisationService.saveOrganisationSet(organisationSet)
				.subscribe(
					(result: OrganisationSet) => {
						vm.log.success('Organisation set saved', organisationSet, 'Save set');
						vm.selectedOrganisationSet.organisations = organisationSet.organisations;
					},
					(error) => vm.log.error('Failed to save set', error, 'Save set')
				);
		});
	}

	deleteSet(item: OrganisationSet) {
		let vm = this;
		MessageBoxDialog.open(vm.$modal,
			'Delete Organisation Set', 'Are you sure you want to delete the set?', 'Yes', 'No')
			.result.then(function () {
			vm.organisationService.deleteOrganisationSet(item)
				.subscribe(() => {
						let i = vm.organisationSets.indexOf(item);
						vm.organisationSets.splice(i, 1);
						vm.log.success('Organisation set deleted', item, 'Delete set');
					},
					(error) => vm.log.error('Failed to delete set', error, 'Delete set')
				);
		});
	}
}
