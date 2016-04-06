/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.reports {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import ILoggerService = app.blocks.ILoggerService;
	import FolderContent = app.models.FolderItem;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import IScope = angular.IScope;
	import FolderItem = app.models.FolderItem;
	import FolderType = app.models.FolderType;
	import InputBoxController = app.dialogs.InputBoxController;
	import MessageBoxController = app.dialogs.MessageBoxController;
	import IModalService = angular.ui.bootstrap.IModalService;
	import Folder = app.models.Folder;
	import QueueReportController = app.dialogs.QueueReportController;
	import RequestParameters = app.models.RequestParameters;
	import LoggerService = app.blocks.LoggerService;
	import IReportService = app.core.IReportService;
	import IFolderService = app.core.IFolderService;
	import ReportSchedule = app.models.ReportSchedule;
	import ReportResult = app.models.ReportResult;
	import LibraryItemFolderModuleBase = app.blocks.LibraryItemFolderModuleBase;
	'use strict';

	export class ReportListController extends LibraryItemFolderModuleBase {
		itemSummaryList : ItemSummaryList;
		selectedReport : FolderItem;
		selectedReportSchedules : ReportSchedule[];
		selectedSchedule : ReportSchedule;
		selectedScheduleResults : ReportResult;

		static $inject = ['ReportService', 'FolderService', 'LoggerService', '$scope', '$uibModal'];

		constructor(
			protected reportService:IReportService,
			protected folderService : IFolderService,
			protected logger : ILoggerService,
			protected $scope : IScope,
			protected $modal : IModalService) {
			super(logger, $modal, folderService, FolderType.Report);
		}

		run(item : FolderItem) {
			var vm = this;
			QueueReportController.open(vm.$modal, item.uuid, item.name)
				.result.then(function(result : RequestParameters) {
					vm.scheduleReport(result);
			});
		}

		scheduleReport(requestParameters : RequestParameters) {
			var vm = this;
			vm.reportService.scheduleReport(requestParameters)
				.then(function(result) {
					vm.logger.success('Report queued', result, 'Run report');
				})
				.catch(function(error) {
					vm.logger.error('Error queueing report', error, 'Run report');
				});
		}

		selectFolderItem(item : FolderItem) {
			var vm = this;
			vm.selectedReport = item;
			vm.selectedReportSchedules = null;
			vm.selectedSchedule = null;
			vm.selectedScheduleResults = null;
			vm.reportService.getReportSchedules(item.uuid, 5)
				.then(function(result) {
					vm.selectedReportSchedules = result;
				});
		}

		selectSchedule(schedule : ReportSchedule) {
			var vm = this;
			vm.selectedSchedule = schedule;
			vm.selectedScheduleResults = null;
			vm.reportService.getScheduleResults(schedule.uuid)
				.then(function(results) {
					vm.selectedScheduleResults = results;
				});
		}

		deleteItem(scope : any) {
			var vm = this;
			vm.reportService.deleteReport(scope.$modelValue)
				.then(function(result) {
					scope.remove();
				});
		}
	}

	angular
		.module('app.reports')
		.controller('ReportListController', ReportListController);
}
