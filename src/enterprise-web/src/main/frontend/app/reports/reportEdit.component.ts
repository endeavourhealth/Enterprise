import {Report} from "./models/Report";
import {ItemSummaryList} from "../library/models/ItemSummaryList";
import {FolderNode} from "../folder/models/FolderNode";
import {ReportNode} from "./models/ReportNode";
import {ReportService} from "./report.service";
import {FolderService} from "../folder/folder.service";
import {LoggerService} from "../common/logger.service";
import {Component, ViewChild} from "@angular/core";
import {UuidNameKVP} from "../common/models/UuidNameKVP";
import {ReportItem} from "./models/ReportItem";
import {ItemType} from "../library/models/ItemType";
import {AdminService} from "../admin/admin.service";
import {StateService, Transition} from "ui-router-ng2";
import {LibraryService} from "../library/library.service";
import {FolderItem} from "../library/models/FolderItem";
import {ITreeOptions, TreeComponent, TREE_ACTIONS, TreeModel, TreeNode} from "angular2-tree-component";

@Component({
	template : require('./reportEdit.html')
})
export class ReportEditComponent {
	treeData : FolderNode[];
	selectedNode : FolderNode = null;
	itemSummaryList : ItemSummaryList;
	report : Report = <Report>{};
	reportContent : ReportNode[];
	@ViewChild('reportTree') reportTree: TreeComponent;

	dataSourceMap : any;
	contentTreeOptions : ITreeOptions;
	reportTreeOptions : ITreeOptions;

	constructor(
		protected reportService : ReportService,
		protected folderService : FolderService,
		protected libraryService : LibraryService,
		protected log : LoggerService,
		protected transition : Transition,
		protected adminService : AdminService,
		protected state : StateService) {

		this.contentTreeOptions = {
			displayField : 'name',
			idField : 'uuid',
			allowDrag : true,
			allowDrop : false,
			actionMapping: {
				mouse: {
					drop: null
				}
			}
		};

		this.reportTreeOptions = {
			displayField : 'name',
			childrenField : 'children',
			idField : 'uuid',
			isExpandedField : 'expanded',
			allowDrag : true,
			actionMapping: {
				mouse: {
					drop: (tree, node, $event, { from, to }) => this.customDrop(tree, node, $event, { from, to }, this.log)
				}
			}
		};

		this.getLibraryRootFolders();
		this.performAction(transition.params()['itemAction'], transition.params()['itemUuid']);
	}

	customDrop(tree: TreeModel, node: TreeNode, $event: any, {from, to}: {from: any; to: any; }, log : LoggerService) {
		// Check for matching siblings
		if (!node.data.children.every(
				(sibling : ReportNode) => { return sibling.uuid !== from.data.uuid; }
			)) {
			log.error('A component can only appear once at a given level', null, 'Error dropping component');
			return;
		}

		// Check for parent nesting
		let parent : any = node;
		while (parent != null) {
			if (parent.data.uuid === from.data.uuid) {
				log.error('A component cannot be a child of itself', null, 'Error dropping component');
				return false;
			}
			parent = parent.parent;
		}

		if (node.treeModel === from.treeModel) {
			// do the normal move:
			TREE_ACTIONS.MOVE_NODE(tree, node, $event, {from, to});
		}
		else {

			// Create a new tree node from source
			let newNode : ReportNode = {
				uuid : from.data.uuid,
				name : from.data.name,
				type : from.data.type,
				expanded : true,
				children : []
			};

			// Add to tree
			node.data.children.push(newNode);
			node.treeModel.update();
		}
	}

	// General report methods
	performAction(action : string, itemUuid : string) {
		switch (action) {
			case 'add':
				this.createReport(itemUuid);
				break;
			case 'edit':
				this.getReport(itemUuid);
				break;
		}
	}

	createReport(folderUuid : string) {
		// Initialize blank report
		this.report = {
			uuid: '',
			name: 'New report',
			description: '',
			folderUuid: folderUuid,
			reportItem: []
		};
		this.reportContent = [];
	}

	getReport(reportUuid : string) {
		let vm = this;
		vm.reportService.getReport(reportUuid)
			.subscribe(
				(data) => {
				vm.report = data;
				vm.reportContent = [];
				vm.reportService.getContentNamesForReportLibraryItem(reportUuid)
					.subscribe(
						(data) => {
						vm.dataSourceMap = UuidNameKVP.toAssociativeArray(data.contents);
						vm.populateTreeFromReportLists(vm.report.reportItem, vm.reportContent);
						vm.reportTree.treeModel.update();
				},
				(error) => vm.log.error('Error loading report item names', error, 'Error')
				);
			},
			(error) => vm.log.error('Error loading report', error, 'Error')
			);
	}

	populateTreeFromReportLists(reportItems : ReportItem[], nodeList : ReportNode[]) {
		let vm = this;
		if (reportItems == null) { reportItems = []; }

		for (let i = 0; i < reportItems.length; i++) {
			let reportItem : ReportItem = reportItems[i];

			let uuid : string = null;
			let type : ItemType = null;

			if (reportItem.queryLibraryItemUuid && reportItem.queryLibraryItemUuid !== '') {
				uuid = reportItem.queryLibraryItemUuid;
				type = ItemType.Query;
			} else if (reportItem.listReportLibraryItemUuid && reportItem.listReportLibraryItemUuid !== '') {
				uuid = reportItem.listReportLibraryItemUuid;
				type = ItemType.ListOutput;
			}

			if (uuid != null) {
				let reportNode : ReportNode = {
					uuid : uuid,
					name : vm.dataSourceMap[uuid],
					type : type,
					expanded : true,
					children : []
				};

				nodeList.push(reportNode);
				if (reportItem.reportItem && reportItem.reportItem.length > 0) {
					vm.populateTreeFromReportLists(reportItem.reportItem, reportNode.children);
				}
			}
		}
	}

	save(close : boolean) {
		let vm = this;
		vm.report.reportItem = [];
		vm.populateReportListsFromTree(vm.report.reportItem, vm.reportContent);

		vm.reportService.saveReport(vm.report)
			.subscribe(function (data : Report) {
				vm.report.uuid = data.uuid;
				vm.adminService.clearPendingChanges();
				vm.log.success('Report saved', vm.report, 'Saved');
				if (close) { vm.state.go(vm.transition.from()); }
			},
				(error) => vm.log.error('Error saving report', error, 'Error')
			);
	}

	close() {
		this.adminService.clearPendingChanges();
		this.state.go(this.transition.from());
	}

	populateReportListsFromTree(reportItems : ReportItem[], nodes : ReportNode[]) {
		for (let i = 0; i < nodes.length; i++) {
			let reportItem : ReportItem = {
				queryLibraryItemUuid : null,
				listReportLibraryItemUuid : null,
				reportItem : []
			};

			switch (nodes[i].type) {
				case ItemType.Query:
					reportItem.queryLibraryItemUuid = nodes[i].uuid;
					break;
				case ItemType.ListOutput:
					reportItem.listReportLibraryItemUuid = nodes[i].uuid;
					break;
			}
			reportItems.push(reportItem);
			if (nodes[i].children && nodes[i].children.length > 0) {
				this.populateReportListsFromTree(reportItem.reportItem, nodes[i].children);
			}
		}
	}

	// Library tree methods
	getLibraryRootFolders() {
		let vm = this;
		vm.folderService.getFolders(1, null)
			.subscribe(
				(data) => vm.treeData = data.folders
			);
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
				// filter content by those allowed in reports
				if (vm.itemSummaryList.contents) {
					vm.itemSummaryList.contents = vm.itemSummaryList.contents.filter(vm.validReportItemType);
				}
				node.loading = false;
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
					node.loading = false;
				});
		}
	}

	// Report structure methods
	remove(node : any) {
		var parentNodes = node.parent.data.children;
		var idx = parentNodes.indexOf(node.data, 0);
		if (idx > -1) {
			parentNodes.splice(idx, 1);
			this.reportTree.treeModel.update();
		}
	}

	// Library folder content methods
	validReportItemType(input : FolderItem) : boolean {
		switch (input.type) {
			case ItemType.Query:
			case ItemType.ListOutput:
				return true;
			default:
				return false;
		}
	};
}
