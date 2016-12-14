import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {BaseHttp2Service} from "../core/baseHttp2.service";
import {EngineHistoryItem} from "./models/EngineHistoryItem";
import {FolderItem} from "../library/models/FolderItem";
import {EngineState} from "./models/EngineState";
import {ReportActivityItem} from "../reports/models/ReportActivityItem";

@Injectable()
export class DashboardService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	getEngineHistory():Observable<EngineHistoryItem[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('count', '5');

		return this.httpGet('api/dashboard/getEngineHistory', { search : params });
	}

	getRecentDocumentsData():Observable<FolderItem[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('count', '5');

		return this.httpGet('api/dashboard/getRecentDocuments', { search : params });
	}

	getEngineState():Observable<EngineState> {
		return this.httpGet('api/dashboard/getProcessorStatus');
	}

	getReportActivityData():Observable<ReportActivityItem[]> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('count', '5');

		return this.httpGet('api/dashboard/getReportActivity', { search : params });
	}

	startEngine() : Observable<any> {
		return this.httpPost('api/dashboard/startProcessor');
	}

	stopEngine() : Observable<any> {
		return this.httpPost('api/dashboard/stopProcessor');
	}
}
