/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../models/EngineState.ts" />
/// <reference path="../models/EngineHistoryItem.ts" />
/// <reference path="../models/RecentDocumentItem.ts" />
/// <reference path="../models/ReportActivityItem.ts" />

module app.core {
	import ItemSummaryList = app.models.ItemSummaryList;
	import FolderNode = app.models.FolderNode;
	import Report = app.models.Report;
	import UuidNameKVP = app.models.UuidNameKVP;
	import TermlexCode = app.models.Code;
	'use strict';

	export interface ILibraryService {
		getEngineHistory() : ng.IPromise<app.models.EngineHistoryItem[]>;
		getRecentDocumentsData() : ng.IPromise<app.models.RecentDocumentItem[]>;
		getEngineState() : ng.IPromise<app.models.EngineState>;
		getReportActivityData() : ng.IPromise<app.models.ReportActivityItem[]>;
		getFolders(moduleId : number, folderUuid : string):ng.IPromise<FolderNode[]>;
		getFolderContents(folderId : string):ng.IPromise<ItemSummaryList>;
		saveReport(report : Report):ng.IPromise<Report>;
		getReport(uuid : string):ng.IPromise<Report>;
		getLibraryItemNamesForReport(uuid : string):ng.IPromise<UuidNameKVP[]>;
		saveFolder(folder : FolderNode):ng.IPromise<any>;
	}

	export class LibraryService implements ILibraryService {
		static $inject = ['$http', '$q'];

		constructor(private http:ng.IHttpService, private promise:ng.IQService) {
		}

		getEngineHistory():ng.IPromise<app.models.EngineHistoryItem[]> {
			var defer = this.promise.defer();
			this.http.get('app/core/data/enginehistory.json')
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getRecentDocumentsData():ng.IPromise<app.models.RecentDocumentItem[]> {
			var defer = this.promise.defer();
			this.http.get('app/core/data/recentdocuments.json')
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getEngineState():ng.IPromise<app.models.EngineState> {
			var defer = this.promise.defer();
			this.http.get('app/core/data/enginestate.json')
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getReportActivityData():ng.IPromise<app.models.ReportActivityItem[]> {
			var defer = this.promise.defer();
			this.http.get('app/core/data/reportactivity.json')
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getFolders(moduleId : number, folderUuid : string):ng.IPromise<FolderNode[]> {
			var vm = this;
			var defer = vm.promise.defer();
			var request = {
				params: {
					'folderType': moduleId,
					'parentUuid': folderUuid
				}
			};

			vm.http.get<{folders:FolderNode[]}>('api/folder/getFolders', request)
				.then(function (response) {
					defer.resolve(response.data.folders);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getFolderContents(folderUuid : string):ng.IPromise<ItemSummaryList> {
			var vm = this;
			var defer = vm.promise.defer();
			var request = {
				params: {
					'folderUuid': folderUuid
				}
			};
			vm.http.get('api/folder/getFolderContents', request)
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		saveReport(report: Report):ng.IPromise<Report> {
			var vm = this;
			var defer = vm.promise.defer();

			vm.http.post('api/report/saveReport', report)
				.then(function(response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getReport(uuid : string):ng.IPromise<Report> {
			var vm = this;
			var defer = vm.promise.defer();
			var request = {
				params: {
					'uuid': uuid
				}
			};
			vm.http.get('api/report/getReport', request)
				.then(function(response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getLibraryItemNamesForReport(uuid : string):ng.IPromise<UuidNameKVP[]> {
			var vm = this;
			var defer = vm.promise.defer();
			var request = {
				params: {
					'uuid': uuid
				}
			};
			vm.http.get<{contents:UuidNameKVP[]}>('api/library/getLibraryItemNamesForReport', request)
				.then(function(response) {
					defer.resolve(response.data.contents);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		saveFolder(folder: FolderNode):ng.IPromise<any> {
			var vm = this;
			var defer = vm.promise.defer();

			vm.http.post('api/folder/saveFolder', folder)
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}
	}

	angular
		.module('app.core')
		.service('LibraryService', LibraryService);
}