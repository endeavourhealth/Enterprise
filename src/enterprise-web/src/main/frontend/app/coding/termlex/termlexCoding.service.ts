import {Injectable} from "@angular/core";

import {CodeSetValue} from "../../codeSet/models/CodeSetValue";
import {TermlexSearchResult} from "./TermlexSearchResult";
import {TermlexCode} from "./TermlexCode";
import {Concept} from "../models/Concept";
import {Observable} from "rxjs";
import {Http, URLSearchParams} from "@angular/http";
import {CodingService} from "../coding.service";

@Injectable()
export class TermlexCodingService extends CodingService {
	constructor(http : Http) { super(http); }

	searchCodes(searchData : string) : Observable<CodeSetValue[]> {
		let vm = this;
		let params = new URLSearchParams();
		params.append('term', searchData);
		params.append('maxResultsSize', '20');
		params.append('start', '0');

		let observable = Observable.create(observer => {
			vm.httpGet('http://termlex.org/search/sct', { search : params, withCredentials : false })
				.subscribe(
					(response) => {
					let termlexResult : TermlexSearchResult = response as TermlexSearchResult;
					let matches : CodeSetValue[] = termlexResult.results.map((t) => vm.termlexCodeToCodeSetValue(t));
					observer.next(matches);
				},
				(exception) =>
					observer.error(exception)
			);
		});
		return observable;
	}

	getCodeChildren(id : string) : Observable<CodeSetValue[]> {
		let vm = this;

		let observable = Observable.create(observer => {
			vm.httpGet('http://termlex.org/hierarchy/' + id + '/childHierarchy', { withCredentials : false })
				.subscribe(
					(response) => {
					let termlexResult : TermlexCode[] = response.data as TermlexCode[];
					let matches : CodeSetValue[] = termlexResult.map((t) => vm.termlexCodeToCodeSetValue(t));
					observer.next(matches);
				},
				(exception) => observer.error(exception)
				);
		});


		return observable;
	}

	getCodeParents(id : string): Observable<CodeSetValue[]> {
		let vm = this;
		let observable = Observable.create(observer => {
			vm.httpGet('http://termlex.org/hierarchy/' + id + '/parentHierarchy', { withCredentials : false })
				.subscribe(
				(response) => {
					let termlexResult : TermlexCode[] = response.data as TermlexCode[];
					let matches : CodeSetValue[] = termlexResult.map((t) => vm.termlexCodeToCodeSetValue(t));
					observer.next(matches);
				},
				(exception) => observer.error(exception)
				);
		});

		return observable;
	}

	termlexCodeToCodeSetValue(termlexCode : TermlexCode) : CodeSetValue {
		let codeSetValue : CodeSetValue = {
			code : termlexCode.id,
			includeChildren : null,
			exclusion : null
		};
		return codeSetValue;
	}

	getPreferredTerm(id : string): Observable<Concept> {
		return this.httpGet('http://termlex.org/concepts/' + id + '/?flavour=ID_LABEL', { withCredentials : false });
	}
}
