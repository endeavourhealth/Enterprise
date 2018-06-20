import {Component} from "@angular/core";
import {StateService} from "ui-router-ng2";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {EnterpriseLibraryItem} from "./models/EnterpriseLibraryItem";
import {CohortEditDialog} from "../cohort/cohortEditor.dialog";
import {CohortViewDialog} from "../cohort/cohortViewer.dialog";
import {ReportViewDialog} from "../report/reportViewer.dialog";
import {CohortRun} from "../cohort/models/CohortRun";
import {ReportRun} from "../report/models/ReportRun";
import {CohortService} from "../cohort/cohort.service";
import {LibraryService, LoggerService, MessageBoxDialog, ModuleStateService} from "eds-common-js";
import {ItemSummaryList} from "eds-common-js/dist/library/models/ItemSummaryList";
import {ItemType} from "eds-common-js/dist/folder/models/ItemType";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {FolderNode} from "eds-common-js/dist/folder/models/FolderNode";
import {ActionItem} from "eds-common-js/dist/folder/models/ActionItem";
import {ActionMenuItem} from "eds-common-js/dist/folder/models/ActionMenuItem";
import {ReportRunnerDialog} from "../report/reportRunner.dialog";
import {ReportService} from "../report/report.service";

@Component({
	template : require('./library.html')
})
export class LibraryComponent {
	treeData: FolderNode[];
	selectedFolder: FolderNode;
	itemSummaryList: ItemSummaryList;
	actionMenuItems: ActionMenuItem[];

	constructor(protected libraryService: LibraryService,
							protected cohortService: CohortService,
							protected reportService : ReportService,
							protected logger: LoggerService,
							private $modal: NgbModal,
							protected moduleStateService: ModuleStateService,
							protected $state: StateService) {
		this.actionMenuItems = [
			{type: ItemType.Query, text: 'Add cohort'},
			{type: ItemType.Query, text: 'Add report feature'},
			{type: ItemType.CodeSet, text: 'Add code set '},
			{type: ItemType.Report, text: 'Add report'},
			{type: ItemType.System, text: 'Paste item into this folder'}
		];
	}

	folderChanged($event) {
		this.selectedFolder = $event.selectedFolder;
		this.refresh();
	}

	protected getContents() {
		// TODO : Implement ordering
		if (this.itemSummaryList)
			return this.itemSummaryList.contents;
		else
			return null;
	}

	protected refresh() {
		var vm = this;
		vm.selectedFolder.loading = true;

		vm.libraryService.getFolderContents(vm.selectedFolder.uuid)
			.subscribe(
				(data) => {
					vm.itemSummaryList = data;
					vm.selectedFolder.loading = false;
				});
	}

	actionItem(actionItemProp: ActionItem) {
		this.saveState();
		switch (actionItemProp.type) {
			case ItemType.Query:
				this.$state.go('app.queryEdit', {itemUuid: actionItemProp.uuid, itemAction: actionItemProp.action});
				break;
			case ItemType.CodeSet:
				this.$state.go('app.codeSetEdit', {itemUuid: actionItemProp.uuid, itemAction: actionItemProp.action});
				break;
			case ItemType.Report:
				this.$state.go('app.reportEdit', {itemUuid: actionItemProp.uuid, itemAction: actionItemProp.action});
				break;
			case ItemType.System:
				this.pasteItem(actionItemProp.uuid);
				break;
			default:
				this.logger.error('Invalid item type', actionItemProp.type, 'Item ' + actionItemProp.action);
				break;
		}
	}

	actionItemEdit(uuid: string, type: ItemType, action: string) {
		var actionItemProp: ActionItem = {
			uuid: uuid,
			type: type,
			action: action
		}
		this.actionItem(actionItemProp);
	}

	deleteItem(item: FolderItem) {
		var vm = this;
		vm.libraryService.deleteLibraryItem(item.uuid)
			.subscribe(
				(result) => {
					var i = vm.itemSummaryList.contents.indexOf(item);
					vm.itemSummaryList.contents.splice(i, 1);
					vm.logger.success('Library item deleted', result, 'Delete item');
				},
				(error) => vm.logger.error('Error deleting library item', error, 'Delete item')
			);
	}

	runCohort(item: FolderItem) {
		var vm = this;

		let reportRun: CohortRun = {
			organisation: [],
			population: "",
			baselineDate: "",
			queryItemUuid: "",
			baselineCohortId: ""
		};

		CohortEditDialog.open(vm.$modal, reportRun, item)
			.result.then(function (resultData: CohortRun) {

			item.isRunning = true;

			resultData.queryItemUuid = item.uuid;

			vm.cohortService.runCohort(resultData)
				.subscribe(
					(data) => {
						vm.refresh();
					});
		});
	}

	viewLastRun(item : FolderItem) {
		switch (item.type) {
			case ItemType.Query:
				this.viewCohort(item);
				break;
			case ItemType.Report:
				this.viewReport(item);
				break;
		}
	}

	viewCohort(item: FolderItem) {
		var vm = this;
		console.log(item);

		let cohortRun: CohortRun = {
			organisation: [],
			population: "",
			baselineDate: "",
			queryItemUuid: "",
			baselineCohortId: ""
		};

		CohortViewDialog.open(vm.$modal, cohortRun, item)
			.result.then(function (resultData: CohortRun) {

			console.log(resultData);

		});
	}

	viewReport(item: FolderItem) {
		var vm = this;
		console.log(item);

		let reportRun: ReportRun = {
			organisation: [],
			population: "",
			baselineCohortId: "",
			cohortName: "",
			baselineDate: "",
			reportItemUuid: "",
			scheduled: false,
			scheduleDateTime: null
		};

		ReportViewDialog.open(vm.$modal, reportRun, item)
			.result.then(function (resultData: CohortRun) {

			console.log(resultData);

		});
	}

	runReport(item: FolderItem) {
		let reportRun: ReportRun = {
			organisation: [],
			population: "",
			baselineDate: "",
			reportItemUuid: item.uuid,
			scheduled: false,
			scheduleDateTime: null,
			baselineCohortId : null,
			cohortName: ""
		};

		let vm = this;
		ReportRunnerDialog.open(vm.$modal, reportRun, item).result.then(
			(result) => {
				if (result) vm.executeReport(item, result)
			},
			(error) => vm.logger.error("Error running report", error)
		);
	}

	executeReport(report : FolderItem, reportRun : ReportRun) {
		let vm = this;
		vm.logger.info("Running report " + report.name);
		report.isRunning = true;
		vm.reportService.runReport(reportRun).subscribe(
			(result) => {
				vm.logger.success("Report run");
				report.lastRun = result;
				report.isRunning = false;
			},
			(error) => {
				vm.logger.error("Report failed", error);
				report.isRunning = false;
			}
		);
	}

	saveState() {
		var state = {
			selectedFolder: this.selectedFolder,
			treeData: this.treeData
		};
		this.moduleStateService.setState('library', state);
	}

	cutItem(item: FolderItem) {
		var vm = this;
		vm.libraryService.getLibraryItem(item.uuid)
			.subscribe(
				(libraryItem: EnterpriseLibraryItem) => {
					vm.moduleStateService.setState('libraryClipboard', libraryItem);
					vm.logger.success('Item cut to clipboard', libraryItem, 'Cut');
				},
				(error) => vm.logger.error('Error cutting to clipboard', error, 'Cut')
			);
	}

	copyItem(item: FolderItem) {
		var vm = this;
		vm.libraryService.getLibraryItem(item.uuid)
			.subscribe(
				(libraryItem: EnterpriseLibraryItem) => {
					vm.moduleStateService.setState('libraryClipboard', libraryItem);
					libraryItem.uuid = null;		// Force save as new
					vm.logger.success('Item copied to clipboard', libraryItem, 'Copy');
				},
				(error) => vm.logger.error('Error copying to clipboard', error, 'Copy')
			);
	}

	pasteItem(uuid) {
		var vm = this;
		var libraryItem: EnterpriseLibraryItem = vm.moduleStateService.getState('libraryClipboard') as EnterpriseLibraryItem;
		console.log(uuid);
		console.log(libraryItem);
		if (libraryItem) {
			libraryItem.folderUuid = uuid;
			vm.libraryService.saveLibraryItem(libraryItem)
				.subscribe(
					(result) => {
						vm.logger.success('Item pasted to folder', libraryItem, 'Paste');
						// reload folder if still selection
						if (vm.selectedFolder.uuid === uuid) {
							vm.refresh();
						}
					},
					(error) => vm.logger.error('Error pasting clipboard', error, 'Paste')
				);
		}
	}
}
