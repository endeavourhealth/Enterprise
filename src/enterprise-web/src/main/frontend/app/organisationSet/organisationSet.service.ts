import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {BaseHttp2Service} from "../core/baseHttp2.service";
import {OrganisationSet} from "./models/OrganisationSet";
import {OrganisationSetMember} from "./models/OrganisationSetMember";
import {Observable} from "rxjs";

@Injectable()
export class OrganisationSetService extends BaseHttp2Service {
	constructor (http : Http) { super(http); }

	getOrganisationSets() : Observable<OrganisationSet[]> {
		return this.httpGet('api/lookup/getOrganisationSets');
	}

	getOrganisationSetMembers(uuid : string) : Observable<OrganisationSetMember[]> {
		let params : URLSearchParams = new URLSearchParams();
		params.append('uuid', uuid);

		return this.httpGet('api/lookup/getOrganisationSetMembers', {search : params});
	}

	searchOrganisations(searchCriteria : string) : Observable<OrganisationSetMember[]> {
		let params : URLSearchParams = new URLSearchParams();
		params.append('searchTerm', searchCriteria);

		return this.httpGet('api/lookup/searchOrganisations', {search : params});
	}

	saveOrganisationSet(organisationSet : OrganisationSet) {
		return this.httpPost('api/lookup/saveOrganisationSet', organisationSet);
	}

	deleteOrganisationSet(organisationSet : OrganisationSet) {
		return this.httpPost('api/lookup/deleteOrganisationSet', organisationSet);
	}
}
