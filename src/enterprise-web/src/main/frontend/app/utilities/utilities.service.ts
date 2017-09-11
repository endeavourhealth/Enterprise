import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {BaseHttp2Service} from "eds-common-js";
import {PrevInc} from "./models/PrevInc";

@Injectable()
export class UtilitiesService extends BaseHttp2Service {
	constructor(http: Http) {
		super(http);
	}

	getCodeSets(): Observable<FolderItem[]> {

		return this.httpGet('api/library/getCodeSets');
	}

	runPrevIncReport(options: PrevInc): Observable<any> {
		console.log('sending options');
		return this.httpPost('api/utility/prevInc', options);
	}

	getIncidenceResults(breakdown: string, gender: string[], ethnicity: string[], postcode: string[], lsoa: string[], msoa: string[], agex10: string[]): Observable<any> {
		let body = {
			breakdown: breakdown,
			gender: gender,
			ethnicity: ethnicity,
			postcode: postcode,
			lsoa: lsoa,
			msoa: msoa,
			agex10: agex10
		};
		return this.httpPost('api/utility/incidence', body);
	}

	getPrevalenceResults(breakdown: string, gender: string[], ethnicity: string[], postcode: string[], lsoa: string[], msoa: string[], agex10: string[]): Observable<any> {
		let body = {
			breakdown: breakdown,
			gender: gender,
			ethnicity: ethnicity,
			postcode: postcode,
			lsoa: lsoa,
			msoa: msoa,
			agex10: agex10
		};
		return this.httpPost('api/utility/prevalence', body);
	}

	getDistinctValues(columnName: string): Observable<any> {
		var params: URLSearchParams = new URLSearchParams();
		params.append('columnName', columnName);
		return this.httpGet('api/utility/distinctValues', {search: params});
	}

	addArrayParameter(params: URLSearchParams, name: string, values: string[]) {
		if (values != null && values.length > 0)
			for (let value of values)
				params.append(name, value);
	}
}
