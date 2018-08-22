import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {ReportRun} from "./models/ReportRun";
import {ReportRow} from "./models/ReportRow";
import {ReportResult} from "./models/ReportResult";
import {ReportResultSummary} from "./models/ReportResultSummary";
import {ReportService} from "./report.service";
import {CohortService} from "../cohort/cohort.service";
import {LoggerService} from "eds-common-js";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {MessageBoxDialog} from 'eds-common-js';

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
    reportRow: ReportRow[];
    allReportResults: ReportResultSummary;

    organisations = <any>[];

    constructor(protected reportService: ReportService,
                protected cohortService: CohortService,
                protected $uibModalInstance : NgbActiveModal,
                private $modal: NgbModal,
                private logger : LoggerService) {

    }

    ngOnInit(): void {
        var vm = this;

        vm.cohortService.getOrganisations()
            .subscribe(
                (data) => {
                    vm.organisations = data;
                });

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
                    vm.reportRow = data;
                    console.log(vm.reportRow);
                });
    }

    getOrganisationName(id) {
        var vm = this;
        for (var i = 0, len = vm.organisations.length; i < len; i++) {
            if (vm.organisations[i].id == id) {
                return vm.organisations[i].name;
            }
        }
        return null;
    }

    getSex(id) {
        var sex = "";
        switch(id) {
            case "0":
                sex =  "Male"
                break;
            case "1":
                sex = "Female"
                break;
            default:
                sex = "unknown"
                break;
        }

        return sex;

    }

    getAge(ageYears, ageMonths, ageWeeks) {
        var age = "";

        if (ageYears!=null)
            age = ageYears+" years";
        else if (ageMonths!=null)
            age = ageMonths+" months";
        else if (ageWeeks!=null)
            age = ageWeeks+" weeks";

        return age;

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

    displayPatient(pseudoId) {
        let vm = this;
        vm.cohortService.getNHSNo(pseudoId)
            .subscribe(
                (result) => {
                    console.log(result)
                    for (let value of result)
                        if (value != null) {
                            var message = "Patient NHS Number = "+value;
                            MessageBoxDialog.open(this.$modal, 'Cohort Patient', message, 'Ok', 'Cancel');
                        }
                });

    }

    cancel() {
        this.$uibModalInstance.dismiss('cancel');
    }
}
