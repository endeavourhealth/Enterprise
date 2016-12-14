import {Component, Input} from "@angular/core";
import {DialogBase} from "../dialogs/dialog.base";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {AdminService} from "../admin/admin.service";
import {LoggerService} from "../common/logger.service";
import {OrganisationSetService} from "../organisationSet/organisationSet.service";
import {RequestParameters} from "./models/RequestParameters";
import {OrganisationSetPickerDialog} from "../organisationSet/organisationPicker.dialog";
import {OrganisationSet} from "../organisationSet/models/OrganisationSet";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./queueReport.html')
})
export class QueueReportDialog extends DialogBase {
	@Input() reportUuid : string;
	@Input() reportName : string;

	public static open(modalService: NgbModal, reportUuid : string, reportName : string) {
			const modalRef = modalService.open(QueueReportDialog, { backdrop : "static"});
			modalRef.componentInstance.reportUuid = reportUuid;
			modalRef.componentInstance.reportName = reportName;

			return modalRef;
		}

	patientTypeDisplay : any;
	patientStatusDisplay : any;
	baselineDate : Date;

	constructor(protected $uibModalInstance : NgbActiveModal,
							private $modal : NgbModal,
							private logger : LoggerService,
							private adminService : AdminService,
							private organisationService : OrganisationSetService) {
		super($uibModalInstance);

		this.patientTypeDisplay = {
			regular : 'Regular patients',
			nonRegular : 'Non-regular patients',
			all : 'All patients'
		};

		this.patientStatusDisplay = {
			active : 'Active patients',
			all : 'Active and non-active patients'
		};

		let requestParameters : RequestParameters = {
			reportUuid: this.reportUuid,
			baselineDate: null,
			patientType: 'regular',
			patientStatus: 'active',
			organisation: []
		};

		this.resultData = requestParameters;
	}

	getOrganisationListDisplayText() {
		if (this.resultData.organisation && this.resultData.organisation.length > 0) {
			return this.resultData.organisation.length + ' Organisation(s)';
		} else {
			return 'All Organisations';
		}
	}

	pickOrganisationList() {
		let vm = this;
		OrganisationSetPickerDialog.open(vm.$modal, this.resultData.organisation, null)
			.result.then(function(organisationSet : OrganisationSet) {
				vm.resultData.organisation = organisationSet.organisations;
		});
	}

	clearOrganisationList() {
		this.resultData.organisation = [];
	}

	ok() {
		if (this.baselineDate) {
			this.resultData.baselineDate = this.baselineDate.valueOf();
		} else {
			this.resultData.baselineDate = null;
		}

		super.ok();
	}
}
