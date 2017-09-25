import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {BaseHttp2Service} from "eds-common-js";
import {Observable} from "rxjs/Rx";
import {OrganisationGroup} from "./models/OrganisationGroup";


@Injectable()
export class OrganisationGroupService extends BaseHttp2Service {
    constructor(http: Http) {
        super(http);
    }

    getOrganisationGroups(): Observable<any> {
        return this.httpGet('api/organisationGroup/organisationGroups');
    }

    getAvailableOrganisation(): Observable<any> {
        return this.httpGet('api/organisationGroup/availableOrganisations');
    }

    getOrganisationsInGroup(groupId: number): Observable<any> {
        var params: URLSearchParams = new URLSearchParams();
        params.append('groupId', groupId.toString());
        return this.httpGet('api/organisationGroup/organisationsInGroup', {search: params});
    }

    updateOrganisationGroup(group: OrganisationGroup): Observable<any> {

        return this.httpPost('api/organisationGroup/saveGroup', group);
    }

}