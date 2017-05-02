import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {Organisation} from "./models/Organisation";
import {ReportResult} from "./models/ReportResult";
import {ReportRun} from "./models/ReportRun";
import {BaseHttp2Service} from "eds-common-js";

@Injectable()
export class CohortService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	getOrganisations():Observable<Organisation> {

		return this.httpGet('api/cohort/getOrganisations');
	}

	getCohortResults(queryItemUuid : string, runDate):Observable<ReportResult[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('queryItemUuid', queryItemUuid);
		params.append('runDate', runDate);

		return this.httpGet('api/cohort/getResults', {search : params});
	}

	getAllCohortResults(queryItemUuid : string):Observable<ReportResult> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('queryItemUuid', queryItemUuid);

		return this.httpGet('api/cohort/getAllResults', {search : params});
	}

	getCohortPatients(type : string, queryItemUuid : string, runDate, organisationId):Observable<any[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('type', type);
		params.append('queryItemUuid', queryItemUuid);
		params.append('runDate', runDate);
		params.append('organisationId', organisationId);

		return this.httpGet('api/cohort/getPatients', {search : params});
	}

	runCohort(result : ReportRun):Observable<ReportRun> {
		return this.httpPost('api/cohort/run', result);
	}


}
