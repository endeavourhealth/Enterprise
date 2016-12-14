import {Injectable} from "@angular/core";
import {BaseHttp2Service} from "../core/baseHttp2.service";
import {UserList} from "./models/UserList";
import {User} from "./models/User";
import {Observable} from "rxjs";
import {Http} from "@angular/http";

@Injectable()
export class UserService extends BaseHttp2Service {
	constructor(http : Http) { super (http); }

	getUserList() : Observable<UserList> {
		return this.httpGet('/api/admin/getUsers');
	}

	saveUser(user : User) : Observable<{uuid : string}> {
		return this.httpPost('/api/admin/saveUser', user);
	}
}
