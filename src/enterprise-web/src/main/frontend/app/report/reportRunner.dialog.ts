import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {LoggerService} from "eds-common-js";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {QueryPickerDialog} from "../query/queryPicker.dialog";

@Component({
    selector: 'ngbd-modal-content',
    template: require('./reportRunner.html')
})
export class ReportRunnerDialog {

	public static open(modalService: NgbModal, report: FolderItem) {
		const modalRef = modalService.open(ReportRunnerDialog, {backdrop: "static"});
		modalRef.componentInstance.report = report;

		return modalRef;
	}

	@Input() report = FolderItem;
	cohort: FolderItem;

	constructor(protected $uibModalInstance: NgbActiveModal,
							protected $modal : NgbModal,
							private logger: LoggerService) {

	}

	pickCohort() {
		let vm = this;
		QueryPickerDialog.open(vm.$modal, null).result
			.then(
			(result) => vm.cohort = result,
			(error) => vm.logger.error("Error picking cohort", error)
		);
	}

	ok() {
		this.$uibModalInstance.close(this.cohort);
	}

	cancel() {
		this.$uibModalInstance.dismiss('cancel');
	}
}
