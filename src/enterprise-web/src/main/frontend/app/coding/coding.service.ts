import {Observable} from "rxjs";
import {CodeSetValue} from "../codeSet/models/CodeSetValue";
import {BaseHttp2Service} from "eds-common-js";

export abstract class CodingService extends BaseHttp2Service {
	abstract searchCodes(searchData : string): Observable<CodeSetValue[]>;
	abstract getCodeChildren(code : string): Observable<CodeSetValue[]>;
	abstract getCodeParents(code : string): Observable<CodeSetValue[]>;
	abstract getCodesFromReadList(inclusions : string, exclusions : string) : Observable<CodeSetValue[]>;
}
