/// <reference path="../../typings/tsd.d.ts" />

module app.core {
	export interface ILibraryService {
		getEngineHistory() : ng.IPromise<any>;
		getRecentDocumentsData() : ng.IPromise<any>;
		getEngineState() : ng.IPromise<any>;
		getReportActivityData() : ng.IPromise<any>;
	}

	export class LibraryService implements ILibraryService {
		static $inject = ["$http", "$q"];

		constructor(protected http:ng.IHttpService, protected promise:ng.IQService) {
		}

		getEngineHistory():ng.IPromise<any> {
			var defer = this.promise.defer();
			this.http.get("app/core/data/enginehistory.json")
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getRecentDocumentsData():ng.IPromise<any> {
			var defer = this.promise.defer();
			this.http.get("app/core/data/recentdocuments.json")
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getEngineState():ng.IPromise<any> {
			var defer = this.promise.defer();
			this.http.get("app/core/data/enginestate.json")
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getReportActivityData():ng.IPromise<any> {
			var defer = this.promise.defer();
			this.http.get("app/core/data/reportactivity.json")
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
		.module("app.core")
		.service("LibraryService", LibraryService);
}