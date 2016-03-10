module app.models {
	'use strict';

	class ReportSummary {
		uuid:string;
		name:string;
		folderUuid:string;
	}

	export class ReportXml extends ReportSummary {
		query:Query[];
		listOutput:ListOutput[];
	}

	export class ReportDb extends ReportSummary {
		description : string;
		xmlContent : string;
		isDeleted : boolean;
	}
}