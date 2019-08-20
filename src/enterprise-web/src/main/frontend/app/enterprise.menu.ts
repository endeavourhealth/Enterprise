import {Injectable} from "@angular/core";
import {MenuService} from "eds-common-js";
import {MenuOption} from "eds-common-js/dist/layout/models/MenuOption";

@Injectable()
export class EnterpriseMenuService implements  MenuService {
	getClientId(): string {
		return 'eds-compass';
	}
	getApplicationTitle(): string {
		return 'Population Health v1.8';
	}
	getMenuOptions():MenuOption[] {
		return [
			{caption: 'Library', state: 'app.library', icon: 'fa fa-book'},
			{caption: 'Utilities', state: 'app.utilities', icon: 'fa fa-wrench'},
			{caption: 'FHIR® APIs', state: 'app.dashboard', icon: 'fa fa-cogs', role: 'eds-compass:dashboard'}
		];
	}
}