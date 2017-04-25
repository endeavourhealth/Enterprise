import { Pipe, PipeTransform } from '@angular/core';
import {ItemType} from "eds-common-js/dist/folder/models/ItemType";

@Pipe({name: 'itemTypeIdToString'})
export class ItemTypeIdToStringPipe implements PipeTransform {
	transform (input: number): string {
		switch (input) {
			case ItemType.ReportFolder:
				return 'Folder';
			case ItemType.Report:
				return 'Report';
			case ItemType.Query:
				return 'Cohort';
			case ItemType.Test:
				return 'Test';
			case ItemType.Resource:
				return 'Resource';
			case ItemType.CodeSet:
				return 'Code set';
			case ItemType.DataSet:
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
			case ItemType.ReportFolder:
				return 'fa-folder-open';
			case ItemType.Report:
				return 'fa-file';
			case ItemType.Query:
				return 'fa-question-circle';
			case ItemType.Test:
				return 'fa-random';
			case ItemType.Resource:
				return 'fa-database';
			case ItemType.CodeSet:
				return 'fa-tags';
			case ItemType.DataSet:
				return 'fa-list-alt';
			default:
				return 'text-danger fa-exclamation-triangle';
		}
	};
}
