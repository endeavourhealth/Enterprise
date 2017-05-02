import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {Observable} from "rxjs";
import {BaseHttp2Service} from "eds-common-js";
import {ReportRun} from "./models/ReportRun";

@Injectable()
export class ReportService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	runReport(reportParams : ReportRun):Observable<any> {
		return this.httpPost('api/report/run', reportParams);
	}


}
