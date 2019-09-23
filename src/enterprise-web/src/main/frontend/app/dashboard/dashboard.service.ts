import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {BaseHttp2Service} from "eds-common-js";

@Injectable()
export class DashboardService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }


	getStructuredRecord(params : string):Observable<string> {
		var parameters : URLSearchParams = new URLSearchParams();
		parameters.append('params', params);

		return this.httpGet('api/dashboard/getStructuredRecord', {search : parameters});
	}


}

