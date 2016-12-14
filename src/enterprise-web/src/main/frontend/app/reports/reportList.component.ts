import {Component} from "@angular/core";
import {FolderItem} from "../library/models/FolderItem";
import {ReportSchedule} from "./models/ReportSchedule";
import {ReportResult} from "./models/ReportResult";
import {ReportService} from "./report.service";
import {LoggerService} from "../common/logger.service";
import {FolderService} from "../folder/folder.service";
import {ModuleStateService} from "../common/moduleState.service";
import {StateService} from "ui-router-ng2";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {FolderNode} from "../folder/models/FolderNode";
import {ItemType} from "../library/models/ItemType";
import {RequestParameters} from "./models/RequestParameters";
import {Report} from "./models/Report";
import {ItemSummaryList} from "../library/models/ItemSummaryList";
import {QueueReportDialog} from "./queueReport.dialog";
import {ITreeOptions} from "angular2-tree-component";
import {LibraryService} from "../library/library.service";

@Component({
	template : require('./reportList.html')
})
export class ReportListComponent {
	selectedReport: FolderItem;
	selectedReportSchedules: ReportSchedule[];
	selectedSchedule: ReportSchedule;
	selectedScheduleResults: ReportResult;

	treeData: FolderNode[];
	selectedNode: FolderNode;
	itemSummaryList: ItemSummaryList;
	options : ITreeOptions;

	constructor(protected reportService: ReportService,
							protected folderService: FolderService,
							protected libraryService : LibraryService,
							protected logger: LoggerService,
							protected moduleStateService: ModuleStateService,
							protected $modal: NgbModal,
							protected $state: StateService) {

		this.options = {
			displayField : 'name',
			childrenField : 'childQueries',
			idField : 'uuid',
			isExpandedField : 'isExpanded',
		};

		let state = moduleStateService.getState('reportList');
		if (state) {
			this.treeData = state.treeData;
			this.selectNode(state.selectedNode);

			this.selectedReport = state.selectedReport;
			this.selectedReportSchedules = state.selectedReportSchedules;
			this.selectedSchedule = state.selectedSchedule;
			this.selectedScheduleResults = state.selectedScheduleResults;
		}
	}

	getReportList() {
		// TODO : Sorting
		if (this.itemSummaryList)
			return this.itemSummaryList.contents;
		return null;
	}

	getResults() {
		if (this.selectedScheduleResults)
			return this.selectedScheduleResults.queryResults;
		return null;
	}

	selectNode(node: FolderNode) {
		this.selectedReport = null;
		this.selectedReportSchedules = null;
		this.selectedSchedule = null;
		this.selectedScheduleResults = null;
	}

	actionItem(uuid: string, type: ItemType, action: string) {
		this.saveState();
		switch (type) {
			case ItemType.Report:
				this.$state.go('app.reportEdit', {itemUuid: uuid, itemAction: action});
				break;
			default:
				this.logger.error('Invalid item type', type, 'Item ' + action);
				break;
		}
	}

	run(item: FolderItem) {
		let vm = this;
		QueueReportDialog.open(vm.$modal, item.uuid, item.name)
			.result.then(function (result: RequestParameters) {
			vm.scheduleReport(result);
		});
	}

	scheduleReport(requestParameters: RequestParameters) {
		let vm = this;
		vm.reportService.scheduleReport(requestParameters)
			.subscribe(
				(result) => {
					vm.logger.success('Report queued', result, 'Run report');
					if (requestParameters.reportUuid === vm.selectedReport.uuid) {
						vm.selectFolderItem(vm.selectedReport);
					}
				},
				(error) => vm.logger.error('Error queueing report', error, 'Run report')
			);
	}

	folderChanged($event) {
		if ($event.selectedFolder === this.selectedNode) { return; }
		let vm = this;

		vm.selectedNode = $event.selectedFolder;
		vm.selectedNode.loading = true;

		vm.libraryService.getFolderContents(vm.selectedNode.uuid)
			.subscribe(
				(data) => {
				vm.itemSummaryList = data;
				vm.selectedNode.loading = false;
			});
	}

	selectFolderItem(item: FolderItem) {
		let vm = this;
		vm.selectedReport = item;
		vm.selectedReportSchedules = null;
		vm.selectedSchedule = null;
		vm.selectedScheduleResults = null;
		vm.reportService.getReportSchedules(item.uuid, 5)
			.subscribe(
				(result) => vm.selectedReportSchedules = result,
				(error) => vm.logger.error('Error loading report schedules', error, 'Get schedules')
			);
	}

	selectSchedule(schedule: ReportSchedule) {
		let vm = this;
		vm.selectedSchedule = schedule;
		vm.selectedScheduleResults = null;
		vm.reportService.getScheduleResults(schedule.uuid)
			.subscribe(
				(results) => vm.selectedScheduleResults = results,
				(error) => vm.logger.error('Error schedule results', error, 'Get schedule results')
			);
	}

	deleteItem(item: FolderItem) {
		let vm = this;
		vm.reportService.deleteReport(item.uuid)
			.subscribe(
				(result) => {
					let i = vm.itemSummaryList.contents.indexOf(item);
					vm.itemSummaryList.contents.splice(i, 1);
					vm.logger.success('Report deleted', result, 'Delete report');
				},
				(error) => vm.logger.error('Error deleting report', error, 'Delete report')
			);
	}

	saveState() {
		let state = {
			selectedNode: this.selectedNode,
			treeData: this.treeData,
			selectedReport: this.selectedReport,
			selectedReportSchedules: this.selectedReportSchedules,
			selectedSchedule: this.selectedSchedule,
			selectedScheduleResults: this.selectedScheduleResults
		};
		this.moduleStateService.setState('reportList', state);
	}

	cutItem(item: FolderItem) {
		let vm = this;
		vm.reportService.getReport(item.uuid)
			.subscribe(
				(report: Report) => {
					vm.moduleStateService.setState('reportClipboard', report);
					vm.logger.success('Item cut to clipboard', report, 'Cut');
				},
				(error) => vm.logger.error('Error cutting to clipboard', error, 'Cut')
			);
	}

	copyItem(item: FolderItem) {
		let vm = this;
		vm.reportService.getReport(item.uuid)
			.subscribe(
				(report: Report) => {
					vm.moduleStateService.setState('reportClipboard', report);
					report.uuid = null;		// Force save as new
					vm.logger.success('Item copied to clipboard', report, 'Copy');
				},
				(error) => vm.logger.error('Error copying to clipboard', error, 'Copy')
			);
	}

	pasteItem(node: FolderNode) {
		let vm = this;
		let report: Report = vm.moduleStateService.getState('reportClipboard') as Report;
		if (report) {
			report.folderUuid = node.uuid;
			vm.reportService.saveReport(report)
				.subscribe(
					(result) => {
						vm.logger.success('Item pasted to folder', report, 'Paste');
						// reload folder if still selection
						if (vm.selectedNode.uuid === node.uuid) {
							vm.selectedNode = null;
							vm.selectNode(node);
						}
					},
					(error) => vm.logger.error('Error pasting clipboard', error, 'Paste')
				);
		}
	}
}
