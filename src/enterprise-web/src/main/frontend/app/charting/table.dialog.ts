import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Chart} from "./models/Chart";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./table-dialog.html')
})
export class TableDialog implements OnInit {

	public static open(modalService: NgbModal, tableData : any) {
		const modalRef = modalService.open(TableDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.tableData = tableData;

		return modalRef;
	}

	@Input() tableData;

	constructor(protected $uibModalInstance : NgbActiveModal) {
	}

	ngOnInit(): void {
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
