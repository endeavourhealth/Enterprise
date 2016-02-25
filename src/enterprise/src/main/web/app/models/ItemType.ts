/// <reference path="../../typings/tsd.d.ts" />

module app.models {
	export enum ItemType {
		Folder,
		Report,
		Query,
		Test,
		Datasource,
		CodeSet,
		ListOutput
	}

	export function itemTypeIdToString() {
		return function(input:number):string {
			return ItemType[input];
		};
	}

	angular
		.module('app.models')
		.filter('itemTypeIdToString', itemTypeIdToString);
}

