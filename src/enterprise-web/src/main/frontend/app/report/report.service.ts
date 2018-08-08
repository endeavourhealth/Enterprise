import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {BaseHttp2Service} from "eds-common-js";
import {ReportRun} from "./models/ReportRun";
import {ReportResult} from "./models/ReportResult";
import {ReportRow} from "./models/ReportRow";
import {ReportResultSummary} from "./models/ReportResultSummary";

@Injectable()
export class ReportService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	runReport(reportParams : ReportRun):Observable<any> {
		return this.httpPost('api/report/run', reportParams);
	}

	getAllReportResults(reportItemUuid : string):Observable<ReportResultSummary> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('reportItemUuid', reportItemUuid);

		return this.httpGet('api/report/getAllResults', {search : params});
	}

	getReportResults(reportItemUuid : string, runDate):Observable<ReportRow[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('reportItemUuid', reportItemUuid);
		params.append('runDate', runDate);

		return this.httpGet('api/report/getResults', {search : params});
	}

}
