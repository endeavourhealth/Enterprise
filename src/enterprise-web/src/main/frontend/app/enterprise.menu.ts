import {Injectable} from "@angular/core";
import {MenuService} from "eds-common-js";
import {MenuOption} from "eds-common-js/dist/layout/models/MenuOption";

@Injectable()
export class EnterpriseMenuService implements  MenuService {
	getClientId(): string {
		return 'eds-compass';
	}
	getApplicationTitle(): string {
		return 'Population Health v1.6';
	}
	getMenuOptions():MenuOption[] {
		return [
			{caption: 'Dashboard', state: 'app.dashboard', icon: 'fa fa-tachometer', role: 'eds-compass:dashboard'},
			{caption: 'Library', state: 'app.library', icon: 'fa fa-book'},
			{caption: 'Utilities', state: 'app.utilities', icon: 'fa fa-wrench'}
		];
	}
}