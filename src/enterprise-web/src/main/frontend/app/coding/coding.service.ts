import {Observable} from "rxjs";
import {CodeSetValue} from "../codeSet/models/CodeSetValue";
import {Concept} from "./models/Concept";
import {BaseHttp2Service} from "../core/baseHttp2.service";

export abstract class CodingService extends BaseHttp2Service {
	abstract searchCodes(searchData : string): Observable<CodeSetValue[]>;
	abstract getCodeChildren(code : string): Observable<CodeSetValue[]>;
	abstract getCodeParents(code : string): Observable<CodeSetValue[]>;
	abstract getPreferredTerm(id : string): Observable<Concept>;
}
