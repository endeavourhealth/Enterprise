module app.models {
	'use strict';

	export class ReportNode {
		uuid:string;
		name:string;
		type:number;
		// hasChildren:boolean;
		nodes:ReportNode[];
	}
}