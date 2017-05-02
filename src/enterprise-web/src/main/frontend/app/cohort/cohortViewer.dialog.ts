import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {ReportRun} from "./models/ReportRun";
import {ReportResult} from "./models/ReportResult";
import {ReportPatient} from "./models/ReportPatient";
import {CohortService} from "./cohort.service";
import {LoggerService} from "eds-common-js";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";

@Component({
    selector: 'ngbd-modal-content',
    template: require('./cohortViewer.html')
})
export class CohortViewDialog implements OnInit {

    public static open(modalService: NgbModal, reportRun: ReportRun, item: FolderItem) {
        const modalRef = modalService.open(CohortViewDialog, { backdrop : "static", size : "lg"});
        modalRef.componentInstance.resultData = reportRun;
        modalRef.componentInstance.item = item;

        return modalRef;
    }

    @Input() resultData;
    @Input() item : FolderItem;

    population: string = "";
    baselineDate: string = "";
    runDate: string = "";

    populations = [
        {id: 0, type: 'Currently registered'},
        {id: 1, type: 'All patients'}
    ];

    organisations = <any>[];
    reportResult: ReportResult[];
    allReportResults: ReportResult;
    reportPatient: ReportPatient;

    enumeratorTotal : number = 0;
    denominatorTotal : number = 0;

    constructor(protected reportService: CohortService,
                protected $uibModalInstance : NgbActiveModal,
                private logger : LoggerService) {

    }

    ngOnInit(): void {
        var vm = this;

        vm.reportService.getOrganisations()
            .subscribe(
                (data) => {
                    vm.organisations = data;
                });

        vm.getReportResults(vm.item.uuid, vm.item.lastRun);

        vm.reportService.getAllCohortResults(vm.item.uuid)
            .subscribe(
                (data) => {
                    vm.allReportResults = data;
                });
    }

    getReportResults(uuid:string, lastRun:number) {
        var vm = this;

        vm.reportService.getCohortResults(uuid, lastRun)
            .subscribe(
                (data) => {
                    vm.reportResult = data;
                    vm.baselineDate = vm.reportResult[0].baselineDate;
                    vm.runDate = vm.reportResult[0].runDate;
                    vm.population = vm.reportResult[0].populationTypeId;

                    vm.enumeratorTotal = 0;
                    vm.denominatorTotal = 0;

                    for (var i = 0, len = data.length; i < len; i++) {
                        vm.enumeratorTotal += Number(vm.reportResult[i].enumeratorCount);
                        vm.denominatorTotal += Number(vm.reportResult[i].denominatorCount);
                    }

                });
    }

    getReportPatients(uuid:string, lastRun:number, organisationId:string) {
        var vm = this;

        var type = "PATIENT";

        vm.reportService.getCohortPatients(type, uuid, lastRun, organisationId)
            .subscribe(
                (data) => {
                    var csvData = this.ConvertToCSV(data);
                    var blob = new Blob([csvData], { type: 'text/csv' });
                    var url= window.URL.createObjectURL(blob);
                    window.open(url);
                });
    }

    ConvertToCSV(objArray) {
        var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
        var str = '';
        var row = "";

        for (var i = 0; i < array.length; i++) {
            if (array[i][22]!=null)
                array[i][22] = array[i][22].replace(',','');
            var line = '';
            for (var index in array[i]) {
                if (line != '') line += ','

                line += array[i][index];
            }
            str += line + '\r\n';
        }
        return str;
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

    getPopulationName(id) {
        var vm = this;
        for (var i = 0, len = vm.populations.length; i < len; i++) {
            if (vm.populations[i].id == id) {
                return vm.populations[i].type;
            }
        }
        return null;
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
