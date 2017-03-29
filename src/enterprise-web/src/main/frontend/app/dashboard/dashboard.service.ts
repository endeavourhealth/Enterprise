import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {BaseHttp2Service} from "../core/baseHttp2.service";
import {FolderItem} from "../library/models/FolderItem";

@Injectable()
export class DashboardService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	getRecentDocumentsData():Observable<FolderItem[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('count', '13');

		return this.httpGet('api/dashboard/getRecentDocuments', { search : params });
	}


}
