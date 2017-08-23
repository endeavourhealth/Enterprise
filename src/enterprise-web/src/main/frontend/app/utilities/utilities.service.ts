import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {BaseHttp2Service} from "eds-common-js";

@Injectable()
export class UtilitiesService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	getCodeSets():Observable<FolderItem[]> {

		return this.httpGet('api/library/getCodeSets');
	}


}
