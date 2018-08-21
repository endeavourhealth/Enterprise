import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {CohortRun} from "./models/CohortRun";
import {CohortResult} from "./models/CohortResult";
import {CohortPatient} from "./models/CohortPatient";
import {CohortService} from "./cohort.service";
import {LoggerService} from "eds-common-js";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {MessageBoxDialog} from 'eds-common-js';

@Component({
    selector: 'ngbd-modal-content',
    template: require('./cohortViewer.html')
})
export class CohortViewDialog implements OnInit {

    public static open(modalService: NgbModal, cohortRun: CohortRun, item: FolderItem) {
        const modalRef = modalService.open(CohortViewDialog, { backdrop : "static", size : "lg"});
        modalRef.componentInstance.resultData = cohortRun;
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
    cohortResult: CohortResult[];
    allCohortResults: CohortResult;
    cohortPatient: CohortPatient[];

    enumeratorTotal : number = 0;
    denominatorTotal : number = 0;

    constructor(protected cohortService: CohortService,
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

        console.log(vm.item.lastRun);

        vm.getCohortResults(vm.item.uuid, vm.item.lastRun);

        vm.cohortService.getAllCohortResults(vm.item.uuid)
            .subscribe(
                (data) => {
                    vm.allCohortResults = data;
                    console.log(vm.allCohortResults);
                });
    }

    getCohortResults(uuid:string, lastRun:number) {
        var vm = this;

        vm.cohortService.getCohortResults(uuid, lastRun)
            .subscribe(
                (data) => {
                    vm.cohortResult = data;
                    vm.baselineDate = vm.cohortResult[0].baselineDate;
                    vm.runDate = vm.cohortResult[0].runDate;
                    vm.population = vm.cohortResult[0].populationTypeId;

                    vm.enumeratorTotal = 0;
                    vm.denominatorTotal = 0;

                    for (var i = 0, len = data.length; i < len; i++) {
                        vm.enumeratorTotal += Number(vm.cohortResult[i].enumeratorCount);
                        vm.denominatorTotal += Number(vm.cohortResult[i].denominatorCount);
                    }

                });
    }

    getCohortPatientsCSV(uuid:string, lastRun:number, organisationId:string) {
        var vm = this;

        vm.cohortService.getCohortPatients(uuid, lastRun, organisationId)
            .subscribe(
                (data) => {
                    console.log(data);
                    var csvData = this.ConvertToCSV(data);
                    var blob = new Blob([csvData], { type: 'text/csv' });
                    var url= window.URL.createObjectURL(blob);
                    window.open(url);
                });
    }

    getCohortPatients(uuid:string, lastRun:number, organisationId:string) {
        var vm = this;

        vm.cohortService.getCohortPatients(uuid, lastRun, organisationId)
            .subscribe(
                (data) => {
                    vm.cohortPatient = data;
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

    getSex(id) {
        var sex = "";

        switch(id) {
            case 0:
                sex =  "Male"
                break;
            case 1:
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
