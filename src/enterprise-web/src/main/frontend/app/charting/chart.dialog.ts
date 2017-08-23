import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Chart} from "./models/Chart";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./chart-dialog.html')
})
export class ChartDialog implements OnInit {

	public static open(modalService: NgbModal, title:string, chartData: Chart) {
		const modalRef = modalService.open(ChartDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.title = title;
		modalRef.componentInstance.chartData = chartData;

		return modalRef;
	}

	@Input() title;
	@Input() chartData;

	constructor(protected $uibModalInstance : NgbActiveModal) {
	}

	ngOnInit(): void {
	}

	export() {
		this.chartData.export();
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
