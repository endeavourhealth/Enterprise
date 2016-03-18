/// <reference path="../../typings/tsd.d.ts" />

module app.models {
	export enum CodingSystem {
		EMISReadV2,
		DMD,
		SNOMED
	}

	export function codingSystemIdToString() {
		return function(input:number):string {
			return CodingSystem[input];
		};
	}

	angular
		.module('app.models')
		.filter('codingSystemIdToString', codingSystemIdToString);
}

