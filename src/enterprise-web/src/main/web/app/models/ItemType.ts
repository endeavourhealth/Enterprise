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

	export function itemTypeIdToIcon() {
		return function (input:number):string {
			switch (input) {
				case ItemType.Folder:
					return 'glyphicon-folder-open';
				case ItemType.Report:
					return 'glyphicon-file';
				case ItemType.Query:
					return 'glyphicon-question-sign';
				case ItemType.Test:
					return 'glyphicon-random';
				case ItemType.Datasource:
					return 'glyphicon-hdd';
				case ItemType.CodeSet:
					return 'glyphicon-tags';
				case ItemType.ListOutput:
					return 'glyphicon-list-alt';
			}
		};
	}

	angular
		.module('app.models')
		.filter('itemTypeIdToString', itemTypeIdToString)
		.filter('itemTypeIdToIcon', itemTypeIdToIcon);
}

