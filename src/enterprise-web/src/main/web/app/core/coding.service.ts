/// <reference path="../../typings/tsd.d.ts" />

module app.core {
	import CodeSetValueWithTerm = app.models.CodeSetValueWithTerm;
	import Concept = app.models.Concept;
	'use strict';

	export interface ICodingService {
		searchCodes(searchData : string):ng.IPromise<CodeSetValueWithTerm[]>;
		getCodeChildren(code : string):ng.IPromise<CodeSetValueWithTerm[]>;
		getCodeParents(code : string):ng.IPromise<CodeSetValueWithTerm[]>;
		getPreferredTerm(id : string):ng.IPromise<Concept>;

	}
}