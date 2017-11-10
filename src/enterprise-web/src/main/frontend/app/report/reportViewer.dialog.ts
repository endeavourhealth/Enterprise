import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {ReportRun} from "./models/ReportRun";
import {ReportResult} from "./models/ReportResult";
import {ReportResultSummary} from "./models/ReportResultSummary";
import {ReportService} from "./report.service";
import {CohortService} from "../cohort/cohort.service";
import {LoggerService} from "eds-common-js";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";

@Component({
    selector: 'ngbd-modal-content',
    template: require('./reportViewer.html')
})
export class ReportViewDialog implements OnInit {

    public static open(modalService: NgbModal, reportRun: ReportRun, item: FolderItem) {
        const modalRef = modalService.open(ReportViewDialog, { backdrop : "static", size : "lg"});
        modalRef.componentInstance.resultData = reportRun;
        modalRef.componentInstance.item = item;

        return modalRef;
    }

    @Input() resultData;
    @Input() item : FolderItem;

    runDate: string = "";

    reportResult: ReportResult[];
    allReportResults: ReportResultSummary;

    constructor(protected reportService: ReportService,
                protected $uibModalInstance : NgbActiveModal,
                private logger : LoggerService) {

    }

    ngOnInit(): void {
        var vm = this;

        vm.reportService.getAllReportResults(vm.item.uuid)
            .subscribe(
                (data) => {
                    vm.allReportResults = data;
                });
    }

    getReportResults(uuid:string, lastRun:number) {
        var vm = this;

        vm.reportService.getReportResults(uuid, lastRun)
            .subscribe(
                (data) => {
                    vm.reportResult = data;
                    var csvData = this.ConvertToCSV(vm.reportResult[0].reportOutput);
                    var blob = new Blob([csvData], { type: 'text/csv' });
                    var url= window.URL.createObjectURL(blob);
                    window.open(url);
                });
    }


    getCohortName(params:string) {
        var json = JSON.parse(params);
        return json.cohortName;

    }

    ConvertToCSV(objArray) {
        var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
        var str = '';
        var row = "";

        for (var i = 0; i < array.length; i++) {
            str += array[i] + '\r\n';
        }

        return str;
    }

    save() {
        var vm = this;
        this.ok();
    }

    ok() {
        this.$uibModalInstance.close(this.resultData);
    }

    cancel() {
        this.$uibModalInstance.dismiss('cancel');
    }
}
