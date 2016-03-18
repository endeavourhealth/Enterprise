/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../models/EngineState.ts" />
/// <reference path="../models/EngineHistoryItem.ts" />
/// <reference path="../models/RecentDocumentItem.ts" />
/// <reference path="../models/ReportActivityItem.ts" />

module app.core {
	import TermlexCode = app.models.Code;
	import TermlexSearchResult = app.models.TermlexSearchResult;
	'use strict';

	export interface ICodingService {
		searchCodes(searchData : string):ng.IPromise<app.models.TermlexSearchResult>;
		getCodeChildren(code : string):ng.IPromise<TermlexCode[]>;
		getCodeParents(code : string):ng.IPromise<TermlexCode[]>;
	}

	export class CodingService implements ICodingService {
		static $inject = ['$http', '$q'];

		constructor(private http:ng.IHttpService, private promise:ng.IQService) {
		}
		searchCodes(searchData : string):ng.IPromise<TermlexSearchResult> {
			var defer = this.promise.defer();
			var request = {
				params: {
					'term': searchData,
					'maxResultsSize': 20,
					'start': 0
				},
				withCredentials: false
			};
			this.http.get('http://termlex.org/search/sct', request)
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getCodeChildren(id : string):ng.IPromise<TermlexCode[]> {
			var defer = this.promise.defer();
			var request = {
				withCredentials: false
			};
			this.http.get('http://termlex.org/hierarchy/' + id + '/childHierarchy', request)
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getCodeParents(id : string):ng.IPromise<TermlexCode[]> {
			var defer = this.promise.defer();
			var request = {
				withCredentials: false
			};
			this.http.get('http://termlex.org/hierarchy/' + id + '/parentHierarchy', request)
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
		.service('CodingService', CodingService);
}