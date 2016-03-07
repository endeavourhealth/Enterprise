module app.models {
	'use strict';

	export class Report {
		uuid : string;
		name : string;
		children: ReportNode[] = [];
	}
}