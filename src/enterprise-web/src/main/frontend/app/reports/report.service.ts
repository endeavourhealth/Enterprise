import {Injectable} from "@angular/core";
import {BaseHttp2Service} from "../core/baseHttp2.service";
import {Observable} from "rxjs";
import {Report} from "./models/Report";
import {UuidNameKVP} from "../common/models/UuidNameKVP";
import {RequestParameters} from "./models/RequestParameters";
import {ReportSchedule} from "./models/ReportSchedule";
import {ReportResult} from "./models/ReportResult";
import {URLSearchParams, Http} from "@angular/http";

@Injectable()
export class ReportService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	saveReport(report: Report) : Observable<Report> {
		return this.httpPost('api/report/saveReport', report);
	}

	deleteReport(uuid: string) : Observable<any> {
		let params : URLSearchParams = new URLSearchParams();
		params.append('uuid', uuid);

		return this.httpPost('api/report/deleteReport', {search : params});
	}

	getReport(uuid : string) : Observable<Report> {
		let params : URLSearchParams = new URLSearchParams();
		params.append('uuid', uuid);

		return this.httpGet('api/report/getReport', {search : params});
	}

	getContentNamesForReportLibraryItem(uuid : string) : Observable<{contents : UuidNameKVP[]}> {
		let params : URLSearchParams = new URLSearchParams();
		params.append('uuid', uuid);

		return this.httpGet('api/library/getContentNamesForReportLibraryItem', {search : params});
	}

	scheduleReport(requestParameters : RequestParameters) : Observable<any> {
		return this.httpPost('api/report/scheduleReport', requestParameters);
	}

	getReportSchedules(uuid : string, count : number) : Observable<ReportSchedule[]> {
		let params : URLSearchParams = new URLSearchParams();
		params.append('uuid', uuid);
		params.append('count', count.toString());

		return this.httpGet('api/report/getReportSchedules', {search : params});
	}

	getScheduleResults(uuid : string) : Observable<ReportResult> {
		let params : URLSearchParams = new URLSearchParams();
		params.append('uuid', uuid);

		return this.httpGet('api/report/getScheduleResults', {search : params});
	}
}
