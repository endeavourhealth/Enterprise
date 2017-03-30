import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {BaseHttp2Service} from "../core/baseHttp2.service";
import {Observable} from "rxjs";
import {Organisation} from "./models/Organisation";
import {ReportResult} from "./models/ReportResult";
import {ReportRun} from "./models/ReportRun";
import {ReportPatient} from "./models/ReportPatient";

@Injectable()
export class ReportService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	getOrganisations():Observable<Organisation> {

		return this.httpGet('api/library/getOrganisations');
	}

	getReportResults(queryItemUuid : string, runDate):Observable<ReportResult[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('queryItemUuid', queryItemUuid);
		params.append('runDate', runDate);

		return this.httpGet('api/library/getReportResults', {search : params});
	}

	getAllReportResults(queryItemUuid : string):Observable<ReportResult> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('queryItemUuid', queryItemUuid);

		return this.httpGet('api/library/getAllReportResults', {search : params});
	}

	getReportPatients(type : string, queryItemUuid : string, runDate, organisationId):Observable<any[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('type', type);
		params.append('queryItemUuid', queryItemUuid);
		params.append('runDate', runDate);
		params.append('organisationId', organisationId);

		return this.httpGet('api/library/getReportPatients', {search : params});
	}

	runReport(result : ReportRun):Observable<ReportRun> {
		return this.httpPost('api/library/runReport', result);
	}


}
