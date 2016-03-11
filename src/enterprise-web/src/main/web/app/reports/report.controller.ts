/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.reports {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import ILoggerService = app.blocks.ILoggerService;
	import FolderContent = app.models.FolderItem;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import LibraryController = app.library.LibraryController;
	import ICallbacks = AngularUITree.ICallbacks;
	import IEventInfo = AngularUITree.IEventInfo;
	import FolderItem = app.models.FolderItem;
	import ReportNode = app.models.ReportNode;
	import ItemType = app.models.ItemType;
	import ReportDb = app.models.ReportDb;
	import ReportXml = app.models.ReportXml;
	import Query = app.models.Query;
	import ListOutput = app.models.ListOutput;
	'use strict';

	class ReportController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;
		report : ReportDb;
		reportContent : ReportNode[];
		contentTreeCallbackOptions : any;

		static $inject = ['LibraryService', 'LoggerService', '$stateParams', 'uuid'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger : ILoggerService,
			protected $stateParams : any,
			protected uuid : any) {
			this.contentTreeCallbackOptions = {dropped: this.contentTreeDroppedCallback};

			this.getLibraryRootFolders();
			this.performAction($stateParams.itemAction, $stateParams.itemUuid);
		}

		// General report methods
		performAction(action:string, itemUuid:string) {
			switch (action) {
				case 'add':
					this.createReport(itemUuid);
					break;
				case 'view':
					this.loadReport(itemUuid);
					break;
			}
		}

		createReport(folderUuid:string) {
			// Initialize blank report
			this.report = {
				uuid: this.uuid.v4(),
				name: 'New report',
				description: '',
				xmlContent: '',
				folderUuid: folderUuid,
				isDeleted: false
			};
			this.reportContent = [];
		}

		loadReport(reportUuid:string) {
			// Load report from DB
		}

		saveReport() {
			// Generate ReportXML object
			var reportXml : ReportXml = <ReportXml>{};
			reportXml.uuid = this.report.uuid;
			reportXml.name = this.report.name;
			reportXml.folderUuid = this.report.folderUuid;
			reportXml.query = [];
			reportXml.listOutput = [];
			this.populateReportXmlFromTreeNodes(reportXml, '', this.reportContent);

			// convert to XML
			console.log(reportXml);
		}

		populateReportXmlFromTreeNodes(reportXml : ReportXml, parentUuid : string, nodes : ReportNode[]) {
			for (var i = 0; i < nodes.length; i++) {
				switch (nodes[i].type) {
					case ItemType.Query:
						var query : Query = <Query>{};
						query.uuid = nodes[i].uuid;
						query.parentUuid = parentUuid;
						reportXml.query.push(query);
						break;
					case ItemType.ListOutput:
						var listOutput : ListOutput = <ListOutput>{};
						listOutput.uuid = nodes[i].uuid;
						listOutput.parentUuid = parentUuid;
						reportXml.listOutput.push(listOutput);
						break;
				}
				if (nodes[i].children && nodes[i].children.length > 0) {
					this.populateReportXmlFromTreeNodes(reportXml, nodes[i].uuid, nodes[i].children);
				}
			}
		}

		// Library tree methods
		getLibraryRootFolders() {
			var vm = this;
			vm.libraryService.getFolders(1, null)
				.then(function (data) {
					vm.treeData = data;
				});
		}

		selectNode(node : FolderNode) {
			if (node === this.selectedNode) { return; }
			var vm = this;

			if (vm.selectedNode !== null) {
				vm.selectedNode.isSelected = false;
			}
			node.isSelected = true;
			vm.selectedNode = node;
			node.loading = true;
			vm.libraryService.getFolderContents(node.uuid)
				.then(function(data) {
					vm.itemSummaryList = data;
					// filter content by those allowed in reports
					vm.itemSummaryList.contents = vm.itemSummaryList.contents.filter(vm.validReportItemType);
					node.loading = false;
				});
		}

		toggleExpansion(node : FolderNode) {
			if (!node.hasChildren) { return; }

			node.isExpanded = !node.isExpanded;

			if (node.isExpanded && (node.nodes == null || node.nodes.length === 0)) {
				var vm = this;
				var folderId = node.uuid;
				node.loading = true;
				this.libraryService.getFolders(1, folderId)
					.then(function (data) {
						node.nodes = data;
						node.loading = false;
					});
			}
		}

		// Report structure methods
		remove(scope:any) {
			scope.remove();
		}

		// Library folder content methods
		validReportItemType(input:FolderContent):boolean {
			switch (input.type) {
				case ItemType.Query:
				case ItemType.ListOutput:
					return true;
				default:
					return false;
			}
		};

		contentTreeDroppedCallback(eventInfo: IEventInfo) {
			// Convert clone model to report node
			eventInfo.source.cloneModel.children = [];
		}

	}

	angular
		.module('app.reports')
		.controller('ReportController', ReportController);
}