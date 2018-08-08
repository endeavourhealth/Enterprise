import {TermPickerDialog} from "../coding/termPicker.dialog";
import {Component} from "@angular/core";
import {Transition, StateService} from "ui-router-ng2";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {CodingService} from "../coding/coding.service";
import {EnterpriseLibraryItem} from "../enterpriseLibrary/models/EnterpriseLibraryItem";
import {LibraryService, LoggerService} from "eds-common-js";

@Component({
	template : require('./codeSetEditor.html')
})
export class CodeSetEditComponent {
	libraryItem : EnterpriseLibraryItem;
	termCache : any;

	constructor(
		protected libraryService : LibraryService,
		protected logger : LoggerService,
		protected $modal : NgbModal,
		protected state : StateService,
		protected transition : Transition,
		protected codingService : CodingService) {

		this.termCache = {};
		this.performAction(transition.params()['itemAction'], transition.params()['itemUuid']);
	}

	protected performAction(action: string, itemUuid: string) {
		switch (action) {
			case 'add':
				this.create(itemUuid);
				break;
			case 'edit':
				this.load(itemUuid);
				break;
		}
	}

	showCodePicker() {
		let vm = this;
		TermPickerDialog.open(vm.$modal, vm.libraryItem.codeSet.codeSetValue)
			.result.then(function(result) {
				vm.libraryItem.codeSet.codeSetValue = result;
		});
	}

	create(folderUuid: string) {
		this.libraryItem = {
			uuid: null,
			name: 'New item',
			description: '',
			folderUuid: folderUuid,
			codeSet: {
				codingSystem: 'SNOMED_CT',
				codeSetValue: []
			}
		} as EnterpriseLibraryItem;
	}

	load(uuid : string) {
		let vm = this;
		this.create(null);
		vm.libraryService.getLibraryItem<EnterpriseLibraryItem>(uuid)
			.subscribe(
				(libraryItem) => vm.libraryItem = libraryItem,
				(data) => vm.logger.error('Error loading', data, 'Error')
			);
	}

	save(close : boolean) {
		let vm = this;
		vm.libraryService.saveLibraryItem(vm.libraryItem)
			.subscribe(
				(libraryItem) => {
					vm.libraryItem.uuid = libraryItem.uuid;
					vm.logger.success('Item saved', vm.libraryItem, 'Saved');
					if (close) {
						vm.state.go(vm.transition.from());
					}
				},
				(error) => vm.logger.error('Error saving', error, 'Error')
			);
	}

	close() {
		this.state.go(this.transition.from());
	}
}
