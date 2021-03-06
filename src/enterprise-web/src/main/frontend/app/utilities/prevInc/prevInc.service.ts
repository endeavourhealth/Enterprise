import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {BaseHttp2Service} from "eds-common-js";
import {PrevInc} from "../models/PrevInc";
import {Observable} from "rxjs/Rx";
import {OrganisationGroup} from "../../organisationGroup/models/OrganisationGroup";

@Injectable()
export class PrevIncService extends BaseHttp2Service {
	constructor(http: Http) {
		super(http);
	}

	runPrevIncReport(options: PrevInc): Observable<any> {
		console.log('sending options');
		return this.httpPost('api/incidencePrevalenceUtility/prevInc', options);
	}

	getIncidenceResults(breakdown: string, gender: string[], ethnicity: string[], postcode: string[], lsoa: string[], msoa: string[], orgs: string[],  agex10: string[], ccgs: string[]): Observable<any> {
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
		return this.httpPost('api/incidencePrevalenceUtility/incidence', body);
	}

	getPrevalenceResults(breakdown: string, gender: string[], ethnicity: string[], postcode: string[], lsoa: string[], msoa: string[], orgs: string[], agex10: string[], ccgs: string[]): Observable<any> {
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
		return this.httpPost('api/incidencePrevalenceUtility/prevalence', body);
	}

	getPopulationResults(breakdown: string, gender: string[], ethnicity: string[], postcode: string[], lsoa: string[], msoa: string[], orgs: string[], agex10: string[], ccgs: string[]): Observable<any> {
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
		return this.httpPost('api/incidencePrevalenceUtility/population', body);
	}

	getDistinctValues(columnName: string): Observable<any> {
		var params: URLSearchParams = new URLSearchParams();
		params.append('columnName', columnName);
		return this.httpGet('api/incidencePrevalenceUtility/distinctValues', {search: params});
	}

    getIncPrevOptions(): Observable<any> {
        return this.httpGet('api/incidencePrevalenceUtility/getOptions');
    }
}
