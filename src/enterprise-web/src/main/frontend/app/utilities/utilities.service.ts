import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {BaseHttp2Service} from "eds-common-js";
import {PrevInc} from "./models/PrevInc";

@Injectable()
export class UtilitiesService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	getCodeSets():Observable<FolderItem[]> {

		return this.httpGet('api/library/getCodeSets');
	}

    runDiabetesReport(options: PrevInc):Observable<any> {
		console.log('sending options');
        return this.httpPost('api/utility/diabetes', options);
    }

}
