import {Component} from "@angular/core";
import {Transition, StateService} from "ui-router-ng2";
import {EnterpriseLibraryItem} from "../enterpriseLibrary/models/EnterpriseLibraryItem";
import {LibraryService, LoggerService} from "eds-common-js";
import {ReportCohortFeature} from "./models/ReportCohortFeature";
import {QuerySelection} from "../query/models/QuerySelection";
import {QueryPickerDialog} from "../query/queryPicker.dialog";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
	template : require('./reportEditor.html')
})
export class ReportEditComponent {
	libraryItem : EnterpriseLibraryItem;
	termCache : any;
	cohortFeatureNameCache : any[] = [];
	selectedCohortFeature : ReportCohortFeature;

	constructor(
		private $modal: NgbModal,
		protected libraryService : LibraryService,
		protected logger : LoggerService,
		protected state : StateService,
		protected transition : Transition) {

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

	getCohortFeatures() : ReportCohortFeature[] {
		if (this.libraryItem && this.libraryItem.report && this.libraryItem.report.cohortFeature)
			return this.libraryItem.report.cohortFeature;

		return [];
	}

	pickCohortFeature() {
		let vm = this;
		QueryPickerDialog.open(this.$modal, null)
			.result.then(function (resultData: QuerySelection) {

				if (!vm.selectedCohortFeature.fieldName || vm.selectedCohortFeature.fieldName == vm.cohortFeatureNameCache[vm.selectedCohortFeature.cohortFeatureUuid])
					vm.selectedCohortFeature.fieldName = resultData.name;

				vm.selectedCohortFeature.cohortFeatureUuid = resultData.id;
				vm.cohortFeatureNameCache[resultData.id] = resultData.name;
		});
	}

	getCohortFeatureName(uuid : string) {
		if (!uuid)
			return '(Not set)';

		let name : string = this.cohortFeatureNameCache[uuid];

		if (!name)
			name = 'Loading...';

		return name;
	}

	create(folderUuid: string) {
		this.libraryItem = {
			uuid: null,
			name: 'New item',
			description: '',
			folderUuid: folderUuid,
			report : {
				cohortFeature : []
			}
		} as EnterpriseLibraryItem;
	}

	load(uuid : string) {
		let vm = this;
		this.create(null);
		vm.libraryService.getLibraryItem<EnterpriseLibraryItem>(uuid)
			.subscribe(
				(libraryItem) => {
					vm.libraryItem = libraryItem;
					vm.loadCohortFeatureNameCache();
				},
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

	loadCohortFeatureNameCache() {
		let vm = this;
		let itemIds = vm.libraryItem.report.cohortFeature.map(cf => cf.cohortFeatureUuid);
		vm.libraryService.getLibraryItemNames(itemIds)
			.subscribe(
				(nameMap) => vm.cohortFeatureNameCache = nameMap,
				(error) => vm.logger.error('Error loading name cache', error, 'Error')
			)
	}

	addCohortFeature() {
		let newCohortFeature : ReportCohortFeature = new ReportCohortFeature();
		this.libraryItem.report.cohortFeature.push(newCohortFeature);
		this.selectedCohortFeature = newCohortFeature;
		this.pickCohortFeature();
	}

	moveUp(item : ReportCohortFeature) {
		let idx = this.libraryItem.report.cohortFeature.indexOf(item);
		if (idx < 1)
			return;

		this.libraryItem.report.cohortFeature[idx] = this.libraryItem.report.cohortFeature[idx-1];
		this.libraryItem.report.cohortFeature[idx-1] = item;
	}

	moveDown(item : ReportCohortFeature) {
		let idx = this.libraryItem.report.cohortFeature.indexOf(item);
		if (idx >= this.libraryItem.report.cohortFeature.length -1)
			return;

		this.libraryItem.report.cohortFeature[idx] = this.libraryItem.report.cohortFeature[idx+1];
		this.libraryItem.report.cohortFeature[idx+1] = item;
	}

	remove(item : ReportCohortFeature) {
		let idx = this.libraryItem.report.cohortFeature.indexOf(item);
		this.libraryItem.report.cohortFeature.splice(idx,1);
	}
}
