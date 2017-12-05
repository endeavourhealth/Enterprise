import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {BaseHttp2Service} from "eds-common-js";
import {IndDash} from "../models/IndDash";
import {Observable} from "rxjs/Rx";

@Injectable()
export class IndDashService extends BaseHttp2Service {
	constructor(http: Http) {
		super(http);
	}

	getCohortRunOrganisations(cohortId: string) {
		const params: URLSearchParams = new URLSearchParams();
		params.append('itemId', cohortId);
		return this.httpGet('api/cohort/organisations', {search: params});
	}

	getGraphData(cohortId: string, selectedOrgIds: any[]) {
		const params: URLSearchParams = new URLSearchParams();
		params.append('itemId', cohortId);
		for (const orgId of selectedOrgIds) {
			params.append('orgId', orgId)
		}

		return this.httpGet('api/cohort/graphData', {search: params});
	}
}
