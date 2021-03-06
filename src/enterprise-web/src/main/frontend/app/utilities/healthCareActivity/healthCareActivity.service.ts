import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {BaseHttp2Service} from "eds-common-js";
import {Observable} from "rxjs/Rx";
import {HealthCareActivity} from "../models/HealthCareActivity";

@Injectable()
export class HealthCareActivityService extends BaseHttp2Service {
	constructor(http: Http) {
		super(http);
	}

	runHealthCareActivityReport(options: HealthCareActivity): Observable<any> {
		console.log('sending options');
		console.log(options);
		return this.httpPost('api/healthCareActivityUtility/runActivityReport', options);
	}

	getActivityResults(breakdown: string, gender: string[], ethnicity: string[], postcode: string[], lsoa: string[], msoa: string[], services : string[], orgs: string[],  agex10: string[], ccgs: string[], encounterType : string[]): Observable<any> {
		let body = {
			breakdown: breakdown,
			gender: gender,
			ethnicity: ethnicity,
			postcode: postcode,
			lsoa: lsoa,
			msoa: msoa,
			services: services,
			orgs: orgs,
			agex10: agex10,
			ccgs: ccgs,
			encounterType: encounterType
		};
		return this.httpPost('api/healthCareActivityUtility/incidence', body);
	}

	getDistinctValues(columnName: string): Observable<any> {
		var params: URLSearchParams = new URLSearchParams();
		params.append('columnName', columnName);
		return this.httpGet('api/healthCareActivityUtility/distinctValues', {search: params});
	}
}
