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
	recentDocumentsData:FolderItem[];

	constructor(private dashboardService:DashboardService,
							private logger:LoggerService,
							private $state : StateService) {
		this.refresh();
	}

	refresh() {
		this.getRecentDocumentsData();

	}

	getRecentDocumentsData() {
		var vm:DashboardComponent = this;
		vm.recentDocumentsData = null;
		vm.dashboardService.getRecentDocumentsData()
			.subscribe(
				(data:FolderItem[]) => vm.recentDocumentsData = data
			);
	}

	actionItem(item : FolderItem, action : string) {
		switch (item.type) {
			case ItemType.Query:
				this.$state.go('app.queryEdit', {itemUuid: item.uuid, itemAction: action});
				break;
			case ItemType.CodeSet:
				this.$state.go('app.codeSetEdit', {itemUuid: item.uuid, itemAction: action});
				break;
		}
	}
}

