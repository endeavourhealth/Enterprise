import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {BaseHttp2Service} from "../core/baseHttp2.service";
import {MenuOption} from "./models/MenuOption";

@Injectable()
export class LayoutService extends BaseHttp2Service {
	constructor(http : Http) { super (http); }

	pendingChanges : boolean;

	getMenuOptions() : MenuOption[] {
		return [
			{caption: 'Dashboard', state: 'app.dashboard', icon: 'fa fa-tachometer'},
			{caption: 'Library', state: 'app.library', icon: 'fa fa-book'},
			{caption: 'Reports', state: 'app.reports', icon: 'fa fa-files-o'},
			{caption: 'Organisations', state: 'app.organisationSet', icon: 'fa fa-hospital-o'},
			{caption: 'Users', state: 'app.users', icon: 'fa fa-users'},
			// {caption: 'Audit', state: 'audit', icon: 'fa fa-archive'}
		];
	}

	setPendingChanges() : void {
		this.pendingChanges = true;
	}

	clearPendingChanges() : void {
		this.pendingChanges = false;
	}

	getPendingChanges() : boolean {
		return this.pendingChanges;
	}

}