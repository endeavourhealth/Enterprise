module app.models {
	'use strict';

	export class ReportNode {
		// uuid:string;
		itemName:string;
		// itemType:number;
		// hasChildren:boolean;
		nodes:ReportNode[];
	}
}