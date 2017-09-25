import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {BaseHttp2Service} from "eds-common-js";
import {PrevInc} from "../models/PrevInc";
import {Observable} from "rxjs/Rx";

@Injectable()
export class HealthCareActivityService extends BaseHttp2Service {
	constructor(http: Http) {
		super(http);
	}

	runHealthCareActivityReport(options: PrevInc): Observable<any> {
		console.log('sending options');
		return this.httpPost('api/healthCareActivityUtility/healthCareActivityRun', options);
	}

	getActivityResults(breakdown: string, gender: string[], ethnicity: string[], postcode: string[], lsoa: string[], msoa: string[], orgs: string[],  agex10: string[], ccgs: string[]): Observable<any> {
		let body = {
			breakdown: breakdown,
			gender: gender,
			ethnicity: ethnicity,
			postcode: postcode,
			lsoa: lsoa,
			msoa: msoa,
			orgs: orgs,
			agex10: agex10,
			ccgs: ccgs
		};
		return this.httpPost('api/healthCareActivityUtility/healthCareActivity', body);
	}

	getDistinctValues(columnName: string): Observable<any> {
		var params: URLSearchParams = new URLSearchParams();
		params.append('columnName', columnName);
		return this.httpGet('api/healthCareActivityUtility/distinctValues', {search: params});
	}

	getHealthCareActivityOptions(): Observable<any> {
        return this.httpGet('api/healthCareActivityUtility/getOptions');
    }
}
