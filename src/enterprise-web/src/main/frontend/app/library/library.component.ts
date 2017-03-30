import {Component} from "@angular/core";
import {StateService} from "ui-router-ng2";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {ModuleStateService} from "../common/moduleState.service";
import {LibraryService} from "./library.service";
import {LoggerService} from "../common/logger.service";
import {ItemType} from "./models/ItemType";
import {ItemSummaryList} from "./models/ItemSummaryList";
import {FolderNode} from "../folder/models/FolderNode";
import {FolderItem} from "./models/FolderItem";
import {LibraryItem} from "./models/LibraryItem";
import {ActionItem} from "../folder/models/ActionItem";
import {ReportEditDialog} from "../reports/reportEditor.dialog";
import {ReportViewDialog} from "../reports/reportViewer.dialog";
import {ReportRun} from "../reports/models/ReportRun";
import {ReportService} from "../reports/report.service";

@Component({
	template : require('./library.html')
})
export class LibraryComponent {
	treeData: FolderNode[];
	selectedFolder: FolderNode;
	itemSummaryList: ItemSummaryList;

	constructor(protected libraryService: LibraryService,
				protected reportService: ReportService,
							protected logger: LoggerService,
							private $modal: NgbModal,
							protected moduleStateService: ModuleStateService,
							protected $state: StateService) {
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

	actionItem(actionItemProp : ActionItem) {
		this.saveState();
		switch (actionItemProp.type) {
			case ItemType.Query:
				this.$state.go('app.queryEdit', {itemUuid: actionItemProp.uuid, itemAction: actionItemProp.action});
				break;
			case ItemType.CodeSet:
				this.$state.go('app.codeSetEdit', {itemUuid: actionItemProp.uuid, itemAction: actionItemProp.action});
				break;
			default:
				this.logger.error('Invalid item type', actionItemProp.type, 'Item ' + actionItemProp.action);
				break;
		}
	}

	actionItemEdit(uuid : string, type : ItemType, action : string) {
		var actionItemProp : ActionItem = {
			uuid : uuid,
			type : type,
			action : action
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

	runReport(item: FolderItem) {
		var vm = this;

		let reportRun: ReportRun = {
			organisation: [],
			population: "",
			baselineDate: "",
			queryItemUuid: ""
		};

		ReportEditDialog.open(vm.$modal, reportRun, item)
			.result.then(function (resultData: ReportRun) {

			item.isRunning = true;

			resultData.queryItemUuid = item.uuid;

			vm.reportService.runReport(resultData)
				.subscribe(
					(data) => {
						vm.refresh();
					});

		});
	}

	viewReport(item: FolderItem) {
		var vm = this;
		console.log(item);

		let reportRun: ReportRun = {
			organisation: [],
			population: "",
			baselineDate: "",
			queryItemUuid: ""
		};

		ReportViewDialog.open(vm.$modal, reportRun, item)
			.result.then(function (resultData: ReportRun) {

			console.log(resultData);

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
				(libraryItem: LibraryItem) => {
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
				(libraryItem: LibraryItem) => {
					vm.moduleStateService.setState('libraryClipboard', libraryItem);
					libraryItem.uuid = null;		// Force save as new
					vm.logger.success('Item copied to clipboard', libraryItem, 'Copy');
				},
				(error) => vm.logger.error('Error copying to clipboard', error, 'Copy')
			);
	}

	pasteItem(node: FolderNode) {
		var vm = this;
		var libraryItem: LibraryItem = vm.moduleStateService.getState('libraryClipboard') as LibraryItem;
		if (libraryItem) {
			libraryItem.folderUuid = node.uuid;
			vm.libraryService.saveLibraryItem(libraryItem)
				.subscribe(
					(result) => {
						vm.logger.success('Item pasted to folder', libraryItem, 'Paste');
						// reload folder if still selection
						if (vm.selectedFolder.uuid === node.uuid) {
							vm.refresh();
						}
					},
					(error) => vm.logger.error('Error pasting clipboard', error, 'Paste')
				);
		}
	}
}
