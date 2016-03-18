/// <reference path="../../typings/tsd.d.ts" />

module app.core {
	import TermlexCode = app.models.Code;
	import TermlexSearchResult = app.models.TermlexSearchResult;
	'use strict';

	export interface ICodingService {
		searchCodes(searchData : string):ng.IPromise<app.models.TermlexSearchResult>;
		getCodeChildren(code : string):ng.IPromise<TermlexCode[]>;
		getCodeParents(code : string):ng.IPromise<TermlexCode[]>;
	}

	export class CodingService extends BaseHttpService implements ICodingService {

		searchCodes(searchData : string):ng.IPromise<TermlexSearchResult> {
			var request = {
				params: {
					'term': searchData,
					'maxResultsSize': 20,
					'start': 0
				},
				withCredentials: false
			};

			return this.httpGet('http://termlex.org/search/sct', request);
		}

		getCodeChildren(id : string):ng.IPromise<TermlexCode[]> {
			var request = { withCredentials: false };
			return this.httpGet('http://termlex.org/hierarchy/' + id + '/childHierarchy', request);
		}

		getCodeParents(id : string):ng.IPromise<TermlexCode[]> {
			var request = { withCredentials: false };
			return this.httpGet('http://termlex.org/hierarchy/' + id + '/parentHierarchy', request);
		}
	}

	angular
		.module('app.core')
		.service('CodingService', CodingService);
}