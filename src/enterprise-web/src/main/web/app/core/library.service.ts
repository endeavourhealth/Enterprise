/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../models/EngineState.ts" />
/// <reference path="../models/EngineHistoryItem.ts" />
/// <reference path="../models/RecentDocumentItem.ts" />
/// <reference path="../models/ReportActivityItem.ts" />

module app.core {
	import ItemSummaryList = app.models.ItemSummaryList;
	import FolderNode = app.models.FolderNode;
	import ITreeNode = AngularUITree.ITreeNode;
	'use strict';

	export interface ILibraryService {
		getEngineHistory() : ng.IPromise<app.models.EngineHistoryItem[]>;
		getRecentDocumentsData() : ng.IPromise<app.models.RecentDocumentItem[]>;
		getEngineState() : ng.IPromise<app.models.EngineState>;
		getReportActivityData() : ng.IPromise<app.models.ReportActivityItem[]>;
		searchCodes(searchData : string):ng.IPromise<app.models.CodeSearchResult[]>;
		getCodeTreeData(code : string):ng.IPromise<ITreeNode[]>;
		getFolders(moduleId : number, folderUuid : string):ng.IPromise<FolderNode[]>;
		getFolderContents(folderId : string):ng.IPromise<ItemSummaryList>;
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

		searchCodes(searchData : string):ng.IPromise<app.models.CodeSearchResult[]> {
			var defer = this.promise.defer();
			this.http.get('app/core/data/searchResults.json')
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getCodeTreeData(code : string):ng.IPromise<ITreeNode[]> {
			var defer = this.promise.defer();
			this.http.get('app/core/data/treeResults.json')
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

			vm.http.get<any>('api/folder/getFolders', {params: {'folderType': moduleId, 'parentUuid': folderUuid}})
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

	}

	angular
		.module('app.core')
		.service('LibraryService', LibraryService);
}