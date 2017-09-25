import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {BaseHttp2Service} from "eds-common-js";
import {PrevInc} from "./models/PrevInc";
import {Observable} from "rxjs/Rx";
import {OrganisationGroup} from "./models/OrganisationGroup";

@Injectable()
export class UtilitiesService extends BaseHttp2Service {
	constructor(http: Http) {
		super(http);
	}

	getCodeSets(): Observable<FolderItem[]> {
		return this.httpGet('api/library/getCodeSets');
	}


	getOrganisationGroups(): Observable<any> {
		return this.httpGet('api/incidencePrevalenceUtility/organisationGroups');
	}

	getAvailableOrganisation(): Observable<any> {
		return this.httpGet('api/incidencePrevalenceUtility/availableOrganisations');
	}

	getOrganisationsInGroup(groupId: number): Observable<any> {
		var params: URLSearchParams = new URLSearchParams();
		params.append('groupId', groupId.toString());
		return this.httpGet('api/incidencePrevalenceUtility/organisationsInGroup', {search: params});
	}

	updateOrganisationGroup(group: OrganisationGroup): Observable<any> {

		return this.httpPost('api/incidencePrevalenceUtility/saveGroup', group);
	}
}
