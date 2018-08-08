import {Component} from "@angular/core";
import {StateService} from "ui-router-ng2";
import {DashboardService} from "./dashboard.service";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {LoggerService} from "eds-common-js";
import {ItemType} from "eds-common-js/dist/folder/models/ItemType";

@Component({
	template : require('./dashboard.html')
})
export class DashboardComponent {
	constructor(private dashboardService:DashboardService,
							private logger:LoggerService,
							private $state : StateService) {
		this.refresh();
	}

	refresh() {
	}

}

