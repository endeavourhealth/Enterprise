module app.models {
	'use strict';

	export class ReportNode {
		uuid:string;
		itemUuid:string;
		name:string;
		type:number;
		// hasChildren:boolean;
		nodes:ReportNode[];
	}
}