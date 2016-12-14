import {Component} from "@angular/core";
import {StateService} from "ui-router-ng2";
import {ModuleStateService} from "../common/moduleState.service";
import {LibraryService} from "./library.service";
import {LoggerService} from "../common/logger.service";
import {ItemType} from "./models/ItemType";
import {ItemSummaryList} from "./models/ItemSummaryList";
import {FolderNode} from "../folder/models/FolderNode";
import {FolderItem} from "./models/FolderItem";
import {LibraryItem} from "./models/LibraryItem";

@Component({
	template : require('./library.html')
})
export class LibraryComponent {
	treeData: FolderNode[];
	selectedFolder: FolderNode;
	itemSummaryList: ItemSummaryList;

	constructor(protected libraryService: LibraryService,
							protected logger: LoggerService,
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

	actionItem(uuid: string, type: ItemType, action: string) {
		// Type 8 == Protocol!?!?!?
		this.saveState();
		switch (type) {
			case ItemType.Query:
				this.$state.go('app.queryEdit', {itemUuid: uuid, itemAction: action});
				break;
			case ItemType.ListOutput:
				this.$state.go('app.listOutputEdit', {itemUuid: uuid, itemAction: action});
				break;
			case ItemType.CodeSet:
				this.$state.go('app.codeSetEdit', {itemUuid: uuid, itemAction: action});
				break;
			default:
				this.logger.error('Invalid item type', type, 'Item ' + action);
				break;
		}
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
