import {Component, OnInit, ChangeDetectorRef, OnDestroy} from "@angular/core";
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
export class LibraryComponent implements OnInit, OnDestroy {
	treeData: FolderNode[];
	selectedFolder: FolderNode;
	folderName: string;
	itemSummaryList: ItemSummaryList;
	actionMenuItems: ActionMenuItem[];
	recentDocumentsData:FolderItem[];
	timer: any;

	constructor(protected libraryService: LibraryService,
							protected cohortService: CohortService,
							protected reportService : ReportService,
							protected logger: LoggerService,
							private $modal: NgbModal,
							protected moduleStateService: ModuleStateService,
							protected $state: StateService,
							private cdRef:ChangeDetectorRef) {
		this.actionMenuItems = [
			{type: ItemType.Query, text: 'Add cohort'},
			{type: ItemType.Query, text: 'Add report feature'},
			{type: ItemType.CodeSet, text: 'Add code set '},
			{type: ItemType.Report, text: 'Add report'},
			{type: ItemType.System, text: 'Paste item into this folder'}
		];

		this.folderName = "";

		this.timer = setInterval(() => { this.refresh(); }, 1000 * 15 * 1);

	}

	ngAfterViewChecked()
	{
		if (this.selectedFolder!=null)
			this.folderName = this.selectedFolder.folderName;

		this.cdRef.detectChanges();
	}

	ngOnInit(): void {
		this.getRecentDocumentsData();

	}

	ngOnDestroy(): void {
		console.log('cancelling refresh...');
		clearInterval(this.timer);
	}

	folderChanged($event) {
		this.selectedFolder = $event.selectedFolder;

		this.refresh();
	}

	protected getContents() {

		if (this.itemSummaryList)
			return this.itemSummaryList.contents;
		else
			return null;
	}

	protected refresh() {
		console.log('refreshing...');

		var vm = this;

		vm.libraryService.getFolderContents(vm.selectedFolder.uuid)
			.subscribe(
				(data) => {
					vm.itemSummaryList = data;
				});

		this.getRecentDocumentsData();

	}

	getRecentDocumentsData() {
		this.recentDocumentsData = null;
		this.reportService.getRecentDocumentsData()
			.subscribe(
				(data:FolderItem[]) => this.recentDocumentsData = data
			);
	}

	actionDashboardItem(item : FolderItem, action : string) {
		switch (item.type) {
			case ItemType.Query:
				this.$state.go('app.queryEdit', {itemUuid: item.uuid, itemAction: action});
				break;
			case ItemType.CodeSet:
				this.$state.go('app.codeSetEdit', {itemUuid: item.uuid, itemAction: action});
				break;
			case ItemType.Report:
				this.$state.go('app.reportEdit', {itemUuid: item.uuid, itemAction: action});
				break;
		}
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
			organisationGroup: "",
			population: "",
			baselineDate: "",
			queryItemUuid: "",
			baselineCohortId: ""
		};

		CohortEditDialog.open(vm.$modal, reportRun, item)
			.result.then(function (resultData: CohortRun) {

			resultData.queryItemUuid = item.uuid;

			console.log(resultData);

			vm.logger.info("Running cohort " + item.name);
			item.isRunning = true;

			vm.cohortService.runCohort(resultData).subscribe(
				(data) => {
					vm.logger.success(item.name+" cohort run");
					vm.refresh();
					item.isRunning = false;
				},
				(error) => {
					vm.logger.error(item.name+"cohort failed", error);
					item.isRunning = false;
				}
			);


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
			organisationGroup: "",
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
			organisationGroup: "",
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
		let vm = this;

		let reportRun: ReportRun = {
			organisationGroup: "",
			population: "",
			baselineDate: "",
			reportItemUuid: item.uuid,
			scheduled: false,
			scheduleDateTime: null,
			baselineCohortId : null,
			cohortName: ""
		};

		ReportRunnerDialog.open(vm.$modal, reportRun, item)
			.result.then(function (resultData: ReportRun) {

			vm.logger.info("Running report " + item.name);
			item.isRunning = true;

			vm.reportService.runReport(resultData).subscribe(
				(data) => {
					vm.logger.success(item.name+" report run");
					item.lastRun = data;
					item.isRunning = false;
					vm.refresh();
				},
				(error) => {
					vm.logger.error(item.name+"report failed", error);
					item.isRunning = false;
				}
			);


		});

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
