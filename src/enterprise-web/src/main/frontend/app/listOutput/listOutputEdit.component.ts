import {Component} from "@angular/core";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {Transition, StateService} from "ui-router-ng2";
import {ListReportGroup} from "./models/ListReportGroup";
import {FieldOutput} from "./models/FieldOutput";
import {Field} from "./models/Field";
import {EntityMap} from "./models/EntityMap";
import {AdminService} from "../admin/admin.service";
import {LoggerService} from "../common/logger.service";
import {LibraryService} from "../library/library.service";
import {DataSource} from "../tests/models/DataSource";
import {Test} from "../tests/models/Test";
import {TestEditDialog} from "../tests/testEditor.dialog";
import {Entity} from "./models/Entity";
import {LibraryItem} from "../library/models/LibraryItem";
import {ListReport} from "./models/ListReport";
@Component({
	template : require('./listOutput.html')
})
export class ListOutputEditComponent {
	selectedListReportGroup : ListReportGroup;
	selectedFieldOutput : FieldOutput;
	dataSourceAvailableFields : Field[];
	entityMap : EntityMap;
	libraryItem : LibraryItem = <LibraryItem>{};

	constructor(
		protected libraryService : LibraryService,
		protected logger : LoggerService,
		protected $modal : NgbModal,
		protected adminService : AdminService,
		protected $state : StateService,
		protected transition : Transition) {

		this.loadEntityMap();

		this.performAction(transition.params()['itemAction'], transition.params()['itemUuid']);
	}

	protected performAction(action:string, itemUuid:string) {
		switch (action) {
			case 'add':
				this.create(itemUuid);
				break;
			case 'edit':
				this.load(itemUuid);
				break;
		}
	}

	loadEntityMap() {
		var vm = this;
		vm.libraryService.getEntityMap().subscribe(
			(result : EntityMap) => vm.entityMap = result,
			(error) => vm.logger.error('Error loading entity map', error, 'Error')
		);
	}

	selectDataSource(datasourceContainer : { dataSource : DataSource }) {
		var vm = this;
		var test : Test = null;

		if (datasourceContainer.dataSource) {
			test = {dataSource: datasourceContainer.dataSource} as Test;
		}

		TestEditDialog.open(this.$modal, test, true)
			.result.then(function(dataSourceContainer : { dataSource : DataSource }) {
				datasourceContainer.dataSource = dataSourceContainer.dataSource;
				if (vm.selectedListReportGroup.heading === '') {
					vm.selectedListReportGroup.heading = vm.getDatasourceDisplayName();
				}

				vm.loadDataSourceAvailableFieldList();
				vm.adminService.setPendingChanges();
		});
	}

	getListReportGroupItems() {
		if (this.libraryItem && this.libraryItem.listReport)
			return this.libraryItem.listReport.group;

		return null;
	}

	loadDataSourceAvailableFieldList() {
		// Find entity in entitymap
		this.dataSourceAvailableFields = [];
		var entityName : string = this.selectedListReportGroup.fieldBased.dataSource.entity;

		var matchingEntities : Entity[] = $.grep(this.entityMap.entity, (e) => e.logicalName === entityName);
		if (matchingEntities.length === 1) {
			this.dataSourceAvailableFields = $.grep(matchingEntities[0].field, (e) => e.availability.indexOf('output') > -1);
		}
	}

	getFieldDisplayName(logicalName : string) : string {
		var matchingFields : Field[] = $.grep(this.dataSourceAvailableFields, (e) => e.logicalName === logicalName);
		if (matchingFields.length === 1) {
			return matchingFields[0].displayName;
		}
		return '<Select...>';
	}

	getDatasourceDisplayName() : string {
		if (this.selectedListReportGroup
			&& this.selectedListReportGroup.fieldBased
			&& this.selectedListReportGroup.fieldBased.dataSource) {
			var logicalName = this.selectedListReportGroup.fieldBased.dataSource.entity;
			var matchingEntities:Entity[] = $.grep(this.entityMap.entity, (e) => e.logicalName === logicalName);
			if (matchingEntities.length === 1) {
				return matchingEntities[0].displayName;
			}
		}
		return '<Unknown>';
	}

	addListGroup() {
		this.selectedListReportGroup = {
			heading: '',
			fieldBased: {
				dataSource: null,
				fieldOutput: []
			}
		} as ListReportGroup;
		this.libraryItem.listReport.group.push(this.selectedListReportGroup);
	}

	removeListGroup(scope : any) {
		this.libraryItem.listReport.group.splice(scope.$index, 1);
		if (this.selectedListReportGroup === scope.item) {
			this.selectedListReportGroup = null;
		}
	}

	addFieldOutput() {
		this.selectedFieldOutput = {
			heading : '',
			field : ''
		} as FieldOutput;
		this.selectedListReportGroup.fieldBased.fieldOutput.push(this.selectedFieldOutput);
	}

	removeFieldOutput(scope : any) {
		this.selectedListReportGroup.fieldBased.fieldOutput.splice(scope.$index, 1);
		if (this.selectedFieldOutput === scope.item) {
			this.selectedFieldOutput = null;
		}
	}

	setItemField(item : FieldOutput, field : Field) {
		item.field = field.logicalName;
		if (item.heading === '') {
			item.heading = field.displayName;
		}
	}


	create(folderUuid : string) {
		this.libraryItem = {
			uuid : null,
			name : 'New item',
			description : '',
			folderUuid : folderUuid,
			listReport : {
				group: []
			} as ListReport
		} as LibraryItem;
	}

	load(uuid : string) {
		var vm = this;
		vm.libraryService.getLibraryItem(uuid)
			.subscribe(
				(libraryItem : LibraryItem) => vm.libraryItem = libraryItem,
				(error) => vm.logger.error('Error loading', error, 'Error')
			);
	}

	save(close : boolean) {
		var vm = this;
		vm.libraryService.saveLibraryItem(vm.libraryItem)
			.subscribe(
				(libraryItem: LibraryItem) => {
					vm.libraryItem.uuid = libraryItem.uuid;
					vm.adminService.clearPendingChanges();
					vm.logger.success('Item saved', vm.libraryItem, 'Saved');
					if (close) {
						vm.$state.go(vm.transition.from());
					}
				},
				(error) => vm.logger.error('Error saving', error, 'Error')
			);
	}

	close() {
		this.adminService.clearPendingChanges();
		this.$state.go(this.transition.from());
	}}
