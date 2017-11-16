import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {BaseHttp2Service} from "eds-common-js";
import {IndDash} from "../models/IndDash";
import {Observable} from "rxjs/Rx";
import {OrganisationGroup} from "../../organisationGroup/models/OrganisationGroup";

@Injectable()
export class IndDashService extends BaseHttp2Service {
	constructor(http: Http) {
		super(http);
	}

	runIndDash (options: IndDash): Observable<any> {
		console.log('sending options');
		return this.httpPost('api/dashboard/indDash', options);
	}




}
