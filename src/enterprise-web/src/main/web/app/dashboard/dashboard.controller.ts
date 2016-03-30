/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />
/// <reference path="../blocks/logger.service.ts" />

module app.dashboard {
	import FolderItem = app.models.FolderItem;
	import ItemType = app.models.ItemType;
	'use strict';

	class DashboardController {
		engineHistoryData:app.models.EngineHistoryItem[];
		recentDocumentsData:app.models.FolderItem[];
		engineState:app.models.EngineState;
		reportActivityData:app.models.ReportActivityItem[];

		static $inject = ['LibraryService', 'LoggerService', '$state'];

		constructor(private libraryService:app.core.ILibraryService,
								private logger:app.blocks.ILoggerService,
								private $state : IStateService) {
			this.refresh();
		}

		refresh() {
			this.getEngineHistory();
			this.getRecentDocumentsData();
			this.getEngineState();
			this.getReportActivityData();
		}

		getEngineHistory() {
			var vm:DashboardController = this;
			vm.engineHistoryData = null;
			this.libraryService.getEngineHistory()
				.then(function (data:app.models.EngineHistoryItem[]) {
					vm.engineHistoryData = data;
				});
		}

		getRecentDocumentsData() {
			var vm:DashboardController = this;
			vm.recentDocumentsData = null;
			this.libraryService.getRecentDocumentsData()
				.then(function (data:app.models.FolderItem[]) {
					vm.recentDocumentsData = data;
				});
		}

		getEngineState() {
			var vm:DashboardController = this;
			vm.engineState = null;
			this.libraryService.getEngineState()
				.then(function (data:app.models.EngineState) {
					vm.engineState = data;
				});
		}

		getReportActivityData() {
			var vm:DashboardController = this;
			vm.reportActivityData = null;
			this.libraryService.getReportActivityData()
				.then(function (data:app.models.ReportActivityItem[]) {
					vm.reportActivityData = data;
				});
		}

		actionItem(item : FolderItem, action : string) {
			switch (item.type) {
				case ItemType.Query:
					this.$state.go('app.queryAction', {itemUuid: item.uuid, itemAction: action});
					break;
				case ItemType.ListOutput:
					this.$state.go('app.listOutputAction', {itemUuid: item.uuid, itemAction: action});
					break;
				case ItemType.CodeSet:
					this.$state.go('app.codeSetAction', {itemUuid: item.uuid, itemAction: action});
					break;
				case ItemType.Report:
					this.$state.go('app.reportAction', {itemUuid: item.uuid, itemAction: action});
					break;
			}
		}
	}

	angular
		.module('app.dashboard')
		.controller('DashboardController', DashboardController);
}
