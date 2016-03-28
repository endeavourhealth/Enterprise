/// <reference path="../../typings/tsd.d.ts" />

module app.core {
	import CodeSetValue = app.models.CodeSetValue;
	'use strict';

	export interface ICodingService {
		searchCodes(searchData : string):ng.IPromise<CodeSetValue[]>;
		getCodeChildren(code : string):ng.IPromise<CodeSetValue[]>;
		getCodeParents(code : string):ng.IPromise<CodeSetValue[]>;
	}
}