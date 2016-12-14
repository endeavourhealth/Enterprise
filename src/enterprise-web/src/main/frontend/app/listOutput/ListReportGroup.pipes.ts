import { Pipe, PipeTransform } from '@angular/core';
import {ListReportGroup} from "./models/ListReportGroup";

@Pipe({name: 'listReportGroupToIcon'})
export class ListReportGroupToIcon implements PipeTransform {
	transform (listReportGroup:ListReportGroup) {
		if (listReportGroup.summary != null) {
			return 'fa-summary';
		}
		if (listReportGroup.fieldBased != null) {
			return 'fa-fieldBased';
		}
	};
}

@Pipe({name: 'listReportGroupToTypeName'})
export class ListReportGroupToTypeName implements PipeTransform {
	transform (listReportGroup:ListReportGroup) {
		if (listReportGroup.summary != null) {
			return 'Summary';
		}
		if (listReportGroup.fieldBased != null) {
			return 'Field based';
		}
	};
}

@Pipe({name : 'listReportGroupToDescription'})
export class ListReportGroupToDescription implements PipeTransform {
	transform (listReportGroup:ListReportGroup) {
		if (listReportGroup.summary != null) {
			return 'Summary description';
		}
		if (listReportGroup.fieldBased != null) {
			return ' (' + listReportGroup.fieldBased.fieldOutput.length + ' fields)';
		}
	};
}
