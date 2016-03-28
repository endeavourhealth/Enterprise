/// <reference path="../../typings/tsd.d.ts" />

module app.core {
	import TermlexCode = app.models.TermlexCode;
	import TermlexSearchResult = app.models.TermlexSearchResult;
	import CodeSetValueWithTerm = app.models.CodeSetValueWithTerm;
	import Concept = app.models.Concept;
	'use strict';

	export class TermlexCodingService extends BaseHttpService implements ICodingService {

		searchCodes(searchData : string):ng.IPromise<CodeSetValueWithTerm[]> {
			var vm = this;
			var request = {
				params: {
					'term': searchData,
					'maxResultsSize': 20,
					'start': 0
				},
				withCredentials: false
			};
			var defer = vm.promise.defer();
			vm.http.get('http://termlex.org/search/sct', request)
				.then(function (response) {
					var termlexResult : TermlexSearchResult = response.data as TermlexSearchResult;
					var matches : CodeSetValueWithTerm[] = termlexResult.results.map((t) => vm.termlexCodeToCodeSetValue(t));
					defer.resolve(matches);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getCodeChildren(id : string):ng.IPromise<CodeSetValueWithTerm[]> {
			var vm = this;
			var request = { withCredentials: false };
			var defer = vm.promise.defer();
			vm.http.get('http://termlex.org/hierarchy/' + id + '/childHierarchy', request)
				.then(function (response) {
					var termlexResult : TermlexCode[] = response.data as TermlexCode[];
					var matches : CodeSetValueWithTerm[] = termlexResult.map((t) => vm.termlexCodeToCodeSetValue(t));
					defer.resolve(matches);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getCodeParents(id : string):ng.IPromise<CodeSetValueWithTerm[]> {
			var vm = this;
			var request = { withCredentials: false };
			var defer = vm.promise.defer();
			vm.http.get('http://termlex.org/hierarchy/' + id + '/parentHierarchy', request)
				.then(function (response) {
					var termlexResult : TermlexCode[] = response.data as TermlexCode[];
					var matches : CodeSetValueWithTerm[] = termlexResult.map((t) => vm.termlexCodeToCodeSetValue(t));
					defer.resolve(matches);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		termlexCodeToCodeSetValue(termlexCode : TermlexCode) : CodeSetValueWithTerm {
			var codeSetValue : CodeSetValueWithTerm = {
				code : termlexCode.id,
				term: termlexCode.label,
				includeChildren : null,
				exclusion : null
			};
			return codeSetValue;
		}

		getPreferredTerm(id : string):ng.IPromise<Concept> {
			var request = { withCredentials: false };
			return this.httpGet('http://termlex.org/concepts/' + id + '/?flavour=ID_LABEL', request);
		}
	}

	angular
		.module('app.core')
		.service('CodingService', TermlexCodingService);
}