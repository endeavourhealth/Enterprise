import {Component} from "@angular/core";
import {StateService} from "ui-router-ng2";
import {EngineHistoryItem} from "./models/EngineHistoryItem";
import {FolderItem} from "../library/models/FolderItem";
import {EngineState} from "./models/EngineState";
import {ReportActivityItem} from "../reports/models/ReportActivityItem";
import {DashboardService} from "./dashboard.service";
import {LoggerService} from "../common/logger.service";
import {ItemType} from "../library/models/ItemType";

@Component({
	template : require('./dashboard.html')
})
export class DashboardComponent {
	engineHistoryData:EngineHistoryItem[];
	recentDocumentsData:FolderItem[];
	engineState:EngineState;
	reportActivityData:ReportActivityItem[];

	constructor(private dashboardService:DashboardService,
							private logger:LoggerService,
							private $state : StateService) {
		this.refresh();
	}

	refresh() {
		this.getEngineHistory();
		this.getRecentDocumentsData();
		this.getEngineState();
		this.getReportActivityData();
	}

	getEngineHistory() {
		var vm:DashboardComponent = this;
		vm.engineHistoryData = null;
		vm.dashboardService.getEngineHistory()
			.subscribe(
				(data:EngineHistoryItem[]) => vm.engineHistoryData = data
			);
	}

	getRecentDocumentsData() {
		var vm:DashboardComponent = this;
		vm.recentDocumentsData = null;
		vm.dashboardService.getRecentDocumentsData()
			.subscribe(
				(data:FolderItem[]) => vm.recentDocumentsData = data
			);
	}

	getEngineState() {
		var vm:DashboardComponent = this;
		vm.engineState = null;
		vm.dashboardService.getEngineState()
			.subscribe(
				(data:EngineState) => vm.engineState = data
			);
	}

	getReportActivityData() {
		var vm:DashboardComponent = this;
		vm.reportActivityData = null;
		vm.dashboardService.getReportActivityData()
			.subscribe(
				(data:ReportActivityItem[]) => vm.reportActivityData = data
			);
	}

	startEngine() {
		var vm = this;
		vm.dashboardService.startEngine()
			.subscribe(
				(result) => {
					vm.logger.success('Engine started', result, 'Start engine');
					vm.getEngineState();
				},
				(error) => vm.logger.success('Error starting enging', error, 'Start engine')
			);
	}

	stopEngine() {
		var vm = this;
		vm.dashboardService.stopEngine()
			.subscribe(
				(result) => {
				vm.logger.success('Engine stopped', result, 'Stop engine');
				vm.getEngineState();
			},
			(error) => vm.logger.success('Error stopping enging', error, 'Stop engine')
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
			case ItemType.Report:
				this.$state.go('app.reportEdit', {itemUuid: item.uuid, itemAction: action});
				break;
		}
	}
}

