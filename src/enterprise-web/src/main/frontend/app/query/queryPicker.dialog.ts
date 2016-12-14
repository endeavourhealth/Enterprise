import {Input, Component, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {FolderService} from "../folder/folder.service";
import {FolderNode} from "../folder/models/FolderNode";
import {ItemSummaryList} from "../library/models/ItemSummaryList";
import {FolderType} from "../folder/models/FolderType";
import {FolderItem} from "../library/models/FolderItem";
import {ItemType} from "../library/models/ItemType";
import {LibraryService} from "../library/library.service";
import {QuerySelection} from "./models/QuerySelection";

@Component({
		selector: 'ngbd-modal-content',
		template: require('./queryPicker.html')
})
export class QueryPickerDialog implements OnInit {
		public static open(modalService: NgbModal,  querySelection : QuerySelection) {
				const modalRef = modalService.open(QueryPickerDialog, { backdrop : "static", size : "lg"});
				modalRef.componentInstance.resultData = querySelection;

				return modalRef;
		}

		@Input() resultData;
		treeData : FolderNode[];
		selectedNode : FolderNode;
		itemSummaryList : ItemSummaryList;

		constructor(
				protected folderService: FolderService,
				protected libraryService : LibraryService,
				protected activeModal : NgbActiveModal) {
		}

		ngOnInit(): void {
				this.getRootFolders(FolderType.Library);
		}

		getRootFolders(folderType : FolderType) {
				let vm = this;
				vm.folderService.getFolders(folderType, null)
						.subscribe(
							(data) => {
								vm.treeData = data.folders;

								if (vm.treeData && vm.treeData.length > 0) {
										// Set folder type (not retrieved by API)
										vm.treeData.forEach((item) => { item.folderType = folderType; } );
										// Expand top level by default
										vm.toggleExpansion(vm.treeData[0]);
								}
						});
		}

		toggleExpansion(node : FolderNode) {
				if (!node.hasChildren) { return; }

				node.isExpanded = !node.isExpanded;

				if (node.isExpanded && (node.nodes == null || node.nodes.length === 0)) {
						let vm = this;
						let folderId = node.uuid;
						node.loading = true;
						this.folderService.getFolders(1, folderId)
								.subscribe(
									(data) => {
										node.nodes = data.folders;
										// Set parent folder (not retrieved by API)
										node.nodes.forEach((item) => { item.parentFolderUuid = node.uuid; } );
										node.loading = false;
								});
				}
		}

		folderChanged($event) {
				this.selectNode($event.selectedFolder);
		}

		selectNode(node : FolderNode) {
				if (node === this.selectedNode) { return; }
				let vm = this;

				vm.selectedNode = node;
				node.loading = true;

				vm.libraryService.getFolderContents(node.uuid)
						.subscribe(
							(data) => {
								vm.itemSummaryList = data;
								node.loading = false;
						});
		}

		actionItem(item : FolderItem, action : string) {
				let vm = this;
				switch (item.type) {
						case ItemType.Query:
								let querySelection: QuerySelection = {
										id: item.uuid,
										name: item.name,
										description: item.description
								};
								vm.resultData = querySelection;
								this.ok();
								break;
				}
		}

		getItemSummaryListContents() {
				// TODO : Reintroduce sort and filter
				if (this.itemSummaryList)
						return this.itemSummaryList.contents;
				else
						return null;
		}

		ok() {
				this.activeModal.close(this.resultData);
				console.log('OK Pressed');
		}

		cancel() {
				this.activeModal.dismiss('cancel');
				console.log('Cancel Pressed');
		}
}
