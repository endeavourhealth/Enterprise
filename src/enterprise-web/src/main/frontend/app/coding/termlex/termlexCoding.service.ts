import {Injectable} from "@angular/core";

import {CodeSetValue} from "../../codeSet/models/CodeSetValue";
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

		let observable = Observable.create(observer => {
			vm.httpGet('api/library/getConcepts', { search : params })
				.subscribe(
					(response) => {
						let termlexResult : TermlexCode[] = response as TermlexCode[];
						let matches : CodeSetValue[] = termlexResult.map((t) => vm.termlexCodeToCodeSetValue(t));
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
		let params = new URLSearchParams();
		params.append('id', id);

		let observable = Observable.create(observer => {
			vm.httpGet('api/library/getConceptChildren', { search : params })
				.subscribe(
					(response) => {

					let termlexResult : TermlexCode[] = response as TermlexCode[];
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
		let params = new URLSearchParams();
		params.append('id', id);

		let observable = Observable.create(observer => {
			vm.httpGet('api/library/getConceptParents', { search : params })
				.subscribe(
				(response) => {
					let termlexResult : TermlexCode[] = response as TermlexCode[];
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
			term : termlexCode.label,
			dataType : termlexCode.dataType,
			parentType : termlexCode.parentType,
			baseType : termlexCode.baseType,
			present : termlexCode.present,
			valueFrom : "",
			valueTo : "",
			units : termlexCode.units,
			includeChildren : null,
			exclusion : null
		};
		return codeSetValue;
	}

    getCodesFromReadList(inclusions : string, exclusions : string) : Observable<CodeSetValue[]> {
        let vm = this;
        let params = new URLSearchParams();
        params.append('inclusions', inclusions);
        params.append('exclusions', exclusions);

        let observable = Observable.create(observer => {
            vm.httpGet('api/library/getConceptsFromRead', { search : params })
                .subscribe(
                    (response) => {
                        let termlexResult : TermlexCode[] = response as TermlexCode[];
                        let matches : CodeSetValue[] = termlexResult.map((t) => vm.termlexCodeToCodeSetValue(t));

                        observer.next(matches);
                    },
                    (exception) =>
                        observer.error(exception)
                );
        });
        return observable;
    }


}
