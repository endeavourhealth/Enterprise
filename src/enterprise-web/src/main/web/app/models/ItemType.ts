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
		return function (input:number):string {
			switch (input) {
				case ItemType.Folder:
					return 'Folder';
				case ItemType.Report:
					return 'Report';
				case ItemType.Query:
					return 'Query';
				case ItemType.Test:
					return 'Test';
				case ItemType.Datasource:
					return 'Datasource';
				case ItemType.CodeSet:
					return 'Code set';
				case ItemType.ListOutput:
					return 'List report';
			}
		};
	}

	export function itemTypeIdToIcon() {
		return function (input:number):string {
			switch (input) {
				case ItemType.Folder:
					return 'fa-folder-open';
				case ItemType.Report:
					return 'fa-file';
				case ItemType.Query:
					return 'fa-question-circle';
				case ItemType.Test:
					return 'fa-random';
				case ItemType.Datasource:
					return 'fa-database';
				case ItemType.CodeSet:
					return 'fa-tags';
				case ItemType.ListOutput:
					return 'fa-list-alt';
			}
		};
	}

	angular
		.module('app.models')
		.filter('itemTypeIdToString', itemTypeIdToString)
		.filter('itemTypeIdToIcon', itemTypeIdToIcon);
}

