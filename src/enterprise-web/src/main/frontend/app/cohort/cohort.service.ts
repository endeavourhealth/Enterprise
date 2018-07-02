import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {Organisation} from "./models/Organisation";
import {Lsoa} from "./models/Lsoa";
import {Msoa} from "./models/Msoa";
import {Region} from "./models/Region";
import {CohortResult} from "./models/CohortResult";
import {CohortRun} from "./models/CohortRun";
import {BaseHttp2Service} from "eds-common-js";

@Injectable()
export class CohortService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	getOrganisations():Observable<Organisation> {

		return this.httpGet('api/cohort/getOrganisations');
	}

	getMsoaCodes():Observable<Msoa> {

		return this.httpGet('api/cohort/getMsoaCodes');
	}

	getLsoaCodes():Observable<Lsoa> {

		return this.httpGet('api/cohort/getLsoaCodes');
	}

	getRegions():Observable<Region> {

		return this.httpGet('api/cohort/getRegions');
	}

	getOrgsForRegion(uuid: string):Observable<Organisation> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('uuid', uuid);

		return this.httpGet('api/cohort/getOrgsForRegion', {search : params});
	}

	getOrgsForParentOdsCode(odsCode: string):Observable<Organisation> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('odsCode', odsCode);

		return this.httpGet('api/cohort/getOrgsForParentOdsCode', {search : params});
	}

	getCohortResults(queryItemUuid : string, runDate):Observable<CohortResult[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('queryItemUuid', queryItemUuid);
		params.append('runDate', runDate);

		return this.httpGet('api/cohort/getResults', {search : params});
	}

	getAllCohortResults(queryItemUuid : string):Observable<CohortResult> {
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

	runCohort(result : CohortRun):Observable<CohortRun> {
		return this.httpPost('api/cohort/run', result);
	}

	getFrailty(pseudoId : string):Observable<Boolean> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('pseudoId', pseudoId);
		return this.httpGet('api/cohort/getFrailty', {search : params});
	}


}
