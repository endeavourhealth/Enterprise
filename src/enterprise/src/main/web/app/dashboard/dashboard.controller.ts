/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />
/// <reference path="../blocks/logger.service.ts" />
/// <reference path="dashboard.model.ts"/>

module app.dashboard {
	'use strict';

	class DashboardController {
		_engineHistoryData:EngineHistoryItem[];
		_recentDocumentsData:RecentDocumentItem[];
		_engineState:EngineState;
		_reportActivityData:ReportActivityItem[];

		static $inject = ["LibraryService", "LoggerService"];

		constructor(private libraryService:app.core.ILibraryService, private logger:app.blocks.ILoggerService) {
			this.getEngineHistory();
			this.getRecentDocumentsData();
			this.getEngineState();
			this.getReportActivityData();
			logger.success("Dashboard constructed", "DashData", "Dashboard");
		}

		getEngineHistory() {
			var vm = this;
			this.libraryService.getEngineHistory()
				.then(function (data) {
					vm._engineHistoryData = data;
				});
		}

		getRecentDocumentsData() {
			var vm = this;
			this.libraryService.getRecentDocumentsData()
				.then(function (data) {
					vm._recentDocumentsData = data;
				});
		}

		getEngineState() {
			var vm = this;
			this.libraryService.getEngineState()
				.then(function (data) {
					vm._engineState = data;
				});
		}

		getReportActivityData() {
			var vm = this;
			this.libraryService.getReportActivityData()
				.then(function (data) {
					vm._reportActivityData = data;
				});
		}
	}

	angular
		.module('app.dashboard')
		.controller('DashboardController', DashboardController);
}
