import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {BaseHttp2Service} from "../core/baseHttp2.service";
import {MenuOption} from "./models/MenuOption";

@Injectable()
export class LayoutService extends BaseHttp2Service {
	constructor(http : Http) { super (http); }

	getMenuOptions() : MenuOption[] {
		return [
			{caption: 'Dashboard', state: 'app.dashboard', icon: 'fa fa-tachometer'},
			{caption: 'Library', state: 'app.library', icon: 'fa fa-book'},
		];
	}



}