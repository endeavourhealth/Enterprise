import {Component} from "@angular/core";
import {StateService} from "ui-router-ng2";
import {FolderItem} from "../library/models/FolderItem";
import {DashboardService} from "./dashboard.service";
import {LoggerService} from "../common/logger.service";
import {ItemType} from "../library/models/ItemType";

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
			case ItemType.ListOutput:
				this.$state.go('app.listOutputEdit', {itemUuid: item.uuid, itemAction: action});
				break;
			case ItemType.CodeSet:
				this.$state.go('app.codeSetEdit', {itemUuid: item.uuid, itemAction: action});
				break;
		}
	}
}

