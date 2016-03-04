module app.models {
	'use strict';

	export class Report {
		uuid : string;
		name : string;
		nodes: ReportNode[];
	}
}