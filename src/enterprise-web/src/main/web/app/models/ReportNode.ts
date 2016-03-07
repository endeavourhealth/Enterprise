module app.models {
	'use strict';

	export class ReportNode {
		uuid:string;
		itemUuid:string;
		name:string;
		type:number;
		children:ReportNode[];
	}
}