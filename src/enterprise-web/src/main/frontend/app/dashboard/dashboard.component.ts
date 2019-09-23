import {Component} from "@angular/core";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {StateService} from "ui-router-ng2";
import {DashboardService} from "./dashboard.service";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {LoggerService} from "eds-common-js";
import {ItemType} from "eds-common-js/dist/folder/models/ItemType";
import {MessageBoxDialog} from 'eds-common-js';

@Component({
	template : require('./dashboard.html')
})
export class DashboardComponent {
	constructor(private dashboardService:DashboardService,
							private logger:LoggerService,
							private $modal: NgbModal,
							private $state : StateService) {
		this.refresh();
	}

	result = '';
	params = '{"resourceType": "Parameters", "parameter": [{"name": "patietNHSNumber","valueIdentifier": {"system": "https://fhir.nhs.uk/Id/nhs-number","value": "9999999999"}},{"name": "includeAllergies"},{"name": "includeMedication","part": [{"name": "includePrescriptionIssues","valueBoolean": true},{"name": "medicationSearchFromDate","valueDate": "2017-06-04"}]}]}';
	path='https://discovery.gateway.org/api/Patient/$getstructuredrecord';

	refresh() {
	}

	getStructuredRecord() {
		let vm = this;
		vm.dashboardService.getStructuredRecord(vm.params)
			.subscribe(
				(data) => {
					console.log(data);
					//MessageBoxDialog.open(this.$modal, 'FHIR resource', data, 'Ok', 'Cancel');
					vm.result = data;
				});
	}



}

