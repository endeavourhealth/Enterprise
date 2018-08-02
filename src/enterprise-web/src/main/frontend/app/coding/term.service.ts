import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {Term} from "./models/Term";
import {BaseHttp2Service} from "eds-common-js";

@Injectable()
export class TermService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	getTerms(term : string, snomed : string):Observable<Term[]> {
		console.log(snomed);
		var params : URLSearchParams = new URLSearchParams();
		params.append('term', term);
		params.append('snomed', snomed);
		return this.httpGet('api/library/getTerms', {search : params});
	}

	getTermChildren(code : string): Observable<Term[]>{
		var params : URLSearchParams = new URLSearchParams();
		params.append('code', code);
		return this.httpGet('api/library/getTermChildren', {search : params});
	}

	getTermParents(code : string): Observable<Term[]>{
		var params : URLSearchParams = new URLSearchParams();
		params.append('code', code);
		return this.httpGet('api/library/getTermParents', {search : params});
	}


}
