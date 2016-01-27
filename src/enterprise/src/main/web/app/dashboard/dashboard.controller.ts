/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />
/// <reference path="../blocks/logger.service.ts" />
/// <reference path="../models/EngineState.ts" />
/// <reference path="../models/EngineHistoryItem.ts" />
/// <reference path="../models/RecentDocumentItem.ts" />
/// <reference path="../models/ReportActivityItem.ts" />


module app.dashboard {
	'use strict';

	class DashboardController {
		engineHistoryData:app.models.EngineHistoryItem[];
		recentDocumentsData:app.models.RecentDocumentItem[];
		engineState:app.models.EngineState;
		reportActivityData:app.models.ReportActivityItem[];

		static $inject = ["LibraryService", "LoggerService"];

		constructor(private libraryService:app.core.ILibraryService, private logger:app.blocks.ILoggerService) {
			this.getEngineHistory();
			this.getRecentDocumentsData();
			this.getEngineState();
			this.getReportActivityData();
			logger.success("Dashboard constructed", "DashData", "Dashboard");
		}

		getEngineHistory() {
			var vm:DashboardController = this;
			this.libraryService.getEngineHistory()
				.then(function (data:app.models.EngineHistoryItem[]) {
					vm.engineHistoryData = data;
				});
		}

		getRecentDocumentsData() {
			var vm:DashboardController = this;
			this.libraryService.getRecentDocumentsData()
				.then(function (data:app.models.RecentDocumentItem[]) {
					vm.recentDocumentsData = data;
				});
		}

		getEngineState() {
			var vm:DashboardController = this;
			this.libraryService.getEngineState()
				.then(function (data:app.models.EngineState) {
					vm.engineState = data;
				});
		}

		getReportActivityData() {
			var vm:DashboardController = this;
			this.libraryService.getReportActivityData()
				.then(function (data:app.models.ReportActivityItem[]) {
					vm.reportActivityData = data;
				});
		}
	}

	angular
		.module('app.dashboard')
		.controller('DashboardController', DashboardController);
}
