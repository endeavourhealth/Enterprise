/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />
/// <reference path="library.model.ts"/>

module app.library {
	'use strict';

	class LibraryController {
		_engineHistoryData:EngineHistoryItem[];
		_recentDocumentsData:RecentDocumentItem[];
		_engineState:EngineState;
		_reportActivityData:ReportActivityItem[];

		static $inject = ["LibraryService"];

		constructor(private libraryService:app.core.ILibraryService) {
			this.getEngineHistory();
			this.getRecentDocumentsData();
			this.getEngineState();
			this.getReportActivityData();
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
		.module('app.library')
		.controller('LibraryController', LibraryController);
}
