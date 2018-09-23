import {Input, Component, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {QuerySelection} from "./models/QuerySelection";
import {LibraryService} from "eds-common-js";
import {ItemSummaryList} from "eds-common-js/dist/library/models/ItemSummaryList";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {ItemType} from "eds-common-js/dist/folder/models/ItemType";
import {FolderNode} from "eds-common-js/dist/folder/models/FolderNode";
import {FolderType} from "eds-common-js/dist/folder/models/FolderType";
import {FolderService} from "eds-common-js/dist/folder/folder.service";

@Component({
		selector: 'ngbd-modal-content',
		template: require('./queryPicker.html')
})
export class QueryPickerDialog implements OnInit {
    public static open(modalService: NgbModal, querySelection: QuerySelection) {
        const modalRef = modalService.open(QueryPickerDialog, {backdrop: "static", size: "lg"});
        modalRef.componentInstance.resultData = querySelection;

        return modalRef;
    }

    @Input() resultData;
    treeData: FolderNode[];
    selectedNode: FolderNode;
    itemSummaryList: ItemSummaryList;
    selection: FolderItem;

    constructor(
        protected folderService: FolderService,
        protected libraryService: LibraryService,
        protected activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        this.getRootFolders(FolderType.Library);
    }

    getRootFolders(folderType: FolderType) {
        let vm = this;
        vm.folderService.getFolders(folderType, null)
            .subscribe(
                (data) => {
                    vm.treeData = data.folders;

                    if (vm.treeData && vm.treeData.length > 0) {
                        // Set folder type (not retrieved by API)
                        vm.treeData.forEach((item) => {
                            item.folderType = folderType;
                        });
                        // Expand top level by default
                        vm.toggleExpansion(vm.treeData[0]);
                    }
                });
    }

    toggleExpansion(node: FolderNode) {
        if (!node.hasChildren) {
            return;
        }

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
                        node.nodes.forEach((item) => {
                            item.parentFolderUuid = node.uuid;
                        });
                        node.loading = false;
                    });
        }
    }

    folderChanged($event) {
        this.selectNode($event.selectedFolder);
    }

    selectNode(node: FolderNode) {
        if (node === this.selectedNode) {
            return;
        }
        let vm = this;

        vm.selectedNode = node;
        node.loading = true;

        vm.libraryService.getFolderContents(node.uuid)
            .subscribe(
                (data) => {
                    console.log(data);
                    vm.itemSummaryList = data;
                    node.loading = false;
                });
    }

    select(item: FolderItem) {
        let vm = this;
        switch (item.type) {
            case ItemType.Query:
            case ItemType.CodeSet:
                this.selection = item;
                let querySelection: QuerySelection = {
                    id: item.uuid,
                    name: item.name,
                    description: item.description
                };
                vm.resultData = querySelection;
                break;
            default:
                this.selection = null;
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
        if (this.selection) {
            this.activeModal.close(this.resultData);
            console.log('OK Pressed');
        }
    }

    cancel() {
        this.activeModal.dismiss('cancel');
        console.log('Cancel Pressed');
    }
}
