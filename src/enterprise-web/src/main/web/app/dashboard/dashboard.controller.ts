/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />
/// <reference path="../blocks/logger.service.ts" />

module app.dashboard {
	import FolderItem = app.models.FolderItem;
	import ItemType = app.models.ItemType;
	import IDashboardService = app.core.IDashboardService;
	import ILoggerService = app.blocks.ILoggerService;
	import EngineHistoryItem = app.models.EngineHistoryItem;
	import EngineState = app.models.EngineState;
	import ReportActivityItem = app.models.ReportActivityItem;
	'use strict';

	class DashboardController {
		engineHistoryData:EngineHistoryItem[];
		recentDocumentsData:FolderItem[];
		engineState:EngineState;
		reportActivityData:ReportActivityItem[];

		static $inject = ['DashboardService', 'LoggerService', '$state'];

		constructor(private dashboardService:IDashboardService,
								private logger:ILoggerService,
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
			vm.dashboardService.getEngineHistory()
				.then(function (data:EngineHistoryItem[]) {
					vm.engineHistoryData = data;
				});
		}

		getRecentDocumentsData() {
			var vm:DashboardController = this;
			vm.recentDocumentsData = null;
			vm.dashboardService.getRecentDocumentsData()
				.then(function (data:FolderItem[]) {
					vm.recentDocumentsData = data;
				});
		}

		getEngineState() {
			var vm:DashboardController = this;
			vm.engineState = null;
			vm.dashboardService.getEngineState()
				.then(function (data:EngineState) {
					vm.engineState = data;
				});
		}

		getReportActivityData() {
			var vm:DashboardController = this;
			vm.reportActivityData = null;
			vm.dashboardService.getReportActivityData()
				.then(function (data:ReportActivityItem[]) {
					vm.reportActivityData = data;
				});
		}

		startEngine() {
			var vm = this;
			vm.dashboardService.startEngine()
				.then(function(result) {
					vm.logger.success('Engine started', result, 'Start engine');
					vm.getEngineState();
				})
				.catch(function(error) {
					vm.logger.success('Error starting enging', error, 'Start engine');
				});
		}

		stopEngine() {
			var vm = this;
			vm.dashboardService.stopEngine()
				.then(function(result) {
					vm.logger.success('Engine stopped', result, 'Stop engine');
					vm.getEngineState();
				})
				.catch(function(error) {
					vm.logger.success('Error stopping enging', error, 'Stop engine');
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
