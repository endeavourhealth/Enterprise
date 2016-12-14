import { Pipe, PipeTransform } from '@angular/core';
import {ItemType} from './models/ItemType';

@Pipe({name: 'itemTypeIdToString'})
export class ItemTypeIdToStringPipe implements PipeTransform {
	transform (input: number): string {
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
			default:
				return 'Unknown [' + input + ']';
		}
	};
}

@Pipe({name: 'itemTypeIdToIcon'})
export class ItemTypeIdToIconPipe implements PipeTransform {
	transform (input: number): string {
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
			default:
				return 'text-danger fa-exclamation-triangle';
		}
	};
}
