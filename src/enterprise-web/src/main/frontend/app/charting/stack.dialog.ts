import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Chart} from "./models/Chart";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./stack-dialog.html')
})
export class StackDialog implements OnInit {

	public static open(modalService: NgbModal, title : string, chartData: Chart[]) {
		const modalRef = modalService.open(StackDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.title = title;
		modalRef.componentInstance.chartData = chartData;

		return modalRef;
	}

	@Input() title;
	@Input() chartData: Chart[];

	constructor(protected $uibModalInstance : NgbActiveModal) {
	}

	ngOnInit(): void {
	}

	export() {

		let rowData = [];

		for (let chart of this.chartData) {
			rowData.push(chart.title);
			rowData = rowData.concat(chart.getRowData())
		}

		let blob = new Blob([rowData.join('\n')], { type: 'text/plain' });
		window['saveAs'](blob, this.title + '.csv');
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
