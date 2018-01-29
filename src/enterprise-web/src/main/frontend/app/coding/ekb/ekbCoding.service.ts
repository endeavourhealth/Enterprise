import {CodingService} from "../coding.service";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {CodeSetValue} from "../../codeSet/models/CodeSetValue";
import {Concept} from "../models/Concept";
import {TermlexCode} from "../termlex/TermlexCode";

@Injectable()
export class EkbCodingService extends CodingService {
	constructor (http : Http) { super(http); }

	searchCodes(searchData : string): Observable<CodeSetValue[]> {

		let vm = this;
		let params = new URLSearchParams();
		params.append('term', searchData);
		params.append('maxResultsSize', '20');
		params.append('start', '0');

		console.log('in EKB');

		let observable = Observable.create(observer => {
			vm.httpGet('/api/ekb/search/sct', {search: params, withCredentials: false})
				.subscribe(
					(response) => observer.next(response.data),
					(exception) => observer.error(exception)
				);
		});

		return observable;
	}

	getCodeChildren(id : string): Observable<CodeSetValue[]> {
		let vm = this;

		let observable = Observable.create(observer => {
			vm.httpGet('/api/ekb/hierarchy/' + id + '/childHierarchy')
				.subscribe(
					(response) => observer.next(response.data),
					(exception) => observer.error(exception)
				);
		});
		return observable;
	}

	getCodeParents(id : string): Observable<CodeSetValue[]> {
		let vm = this;

		let observable = Observable.create(observer => {
			vm.httpGet('/api/ekb/hierarchy/' + id + '/parentHierarchy')
				.subscribe(
					(response) => observer.next(response.data),
					(exception) => observer.error(exception)
				);
		});
		return observable;
	}

	getPreferredTerm(id : string): Observable<Concept> {
		return this.httpGet('/api/ekb/concepts/' + id);
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
                        observer.next(response);
                    },
                    (exception) =>
                        observer.error(exception)
                );
        });
        return observable;
    }
}
