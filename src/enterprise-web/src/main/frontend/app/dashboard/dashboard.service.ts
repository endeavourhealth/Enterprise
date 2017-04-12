import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {BaseHttp2Service} from "eds-common-js";

@Injectable()
export class DashboardService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	getRecentDocumentsData():Observable<FolderItem[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('count', '13');

		return this.httpGet('api/dashboard/getRecentDocuments', { search : params });
	}


}
