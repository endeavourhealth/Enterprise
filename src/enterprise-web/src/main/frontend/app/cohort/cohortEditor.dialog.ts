import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {CohortRun} from "./models/CohortRun";
import {Organisation} from "./models/Organisation";
import {CohortService} from "./cohort.service";
import {LoggerService} from "eds-common-js";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {QuerySelection} from "../query/models/QuerySelection";
import {QueryPickerDialog} from "../query/queryPicker.dialog";
import {OrganisationGroup} from "../organisationGroup/models/OrganisationGroup";
import {OrgGroupPickerComponent} from "../organisationGroup/orgGroupPicker.component";
import {OrganisationGroupService} from "../organisationGroup/organisationGroup.service";

@Component({
    selector: 'ngbd-modal-content',
    template: require('./cohortEditor.html')
})
export class CohortEditDialog implements OnInit {

    public static open(modalService: NgbModal, cohortRun: CohortRun, item: FolderItem) {
        const modalRef = modalService.open(CohortEditDialog, { backdrop : "static"});
        modalRef.componentInstance.resultData = cohortRun;
        modalRef.componentInstance.item = item;

        return modalRef;
    }

    @Input() resultData;
    @Input() item = FolderItem;

    population: string = "";
    baselineDate: string = "";
    queryItemUuid: string = "";
    baselineCohort : QuerySelection;
    allOrgs: boolean;

    ppTT: string = "Please select a patient population as the denominator.";
    rdTT: string = "Please specify a run date. i.e. patients registered on or before that date.";
    bcTT: string = "Baseline denominator population.";

    selectedGroupId: number = 1;
    orgGroups: OrganisationGroup[] = [];

    populations = [
        {id: 0, type: 'Currently registered'},
        {id: 1, type: 'All patients'}
    ];

    constructor(protected cohortService: CohortService,
                private orgGroupService: OrganisationGroupService,
                private $modal: NgbModal,
                protected $uibModalInstance : NgbActiveModal,
                private logger : LoggerService) {

    }

    ngOnInit(): void {
        var vm = this;

        vm.resultData.population = 0;
        vm.baselineDate = this.formatDate(new Date());
        console.log(vm.baselineDate);

        vm.getOrganisationGroups();
    }

    getOrganisationGroups() {
        var vm = this;
        vm.orgGroups = [];
        vm.orgGroupService.getOrganisationGroups()
            .subscribe(
                (result) => {
                    for (let value of result) {
                        if (value != null) {
                            vm.orgGroups.push({id: value[0], name: value[1]});
                        }
                    }
                });
    }

    orgManager() {
        var vm = this;
        OrgGroupPickerComponent.open(this.$modal, vm.selectedGroupId ).result.then(
            (result) => {
                if (result) {
                    vm.selectedGroupId = result;
                    vm.getOrganisationGroups();
                    console.log(vm.selectedGroupId);
                }
                else
                    console.log('Cancelled');
            },
            (error) => vm.logger.error("Error running utility", error)
        );
    }

    formatDate(date) {
        var d = new Date(date),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2) month = '0' + month;
        if (day.length < 2) day = '0' + day;

        return [year, month, day].join('-');
    }

    pickBaselineCohort() {
        let vm = this;
        QueryPickerDialog.open(this.$modal, null)
            .result.then((resultData: QuerySelection) => vm.baselineCohort = resultData);
    }

       setSelectedPopulation(selectElement) {
        var vm = this;
        vm.resultData.population = "";
        for (var i = 0; i < selectElement.options.length; i++) {
            var optionElement = selectElement.options[i];
            if (optionElement.selected) {
                vm.resultData.population = optionElement.value;
            }
        }
    }

    save() {
        var vm = this;
        vm.resultData.baselineDate = vm.baselineDate;
        vm.resultData.organisationGroup = vm.selectedGroupId;
        if (vm.baselineCohort)
            vm.resultData.baselineCohortId = vm.baselineCohort.id;

        this.ok();
    }

    ok() {
        var vm = this;
        if (vm.allOrgs)
            vm.resultData.organisationGroup = "0";
        this.$uibModalInstance.close(this.resultData);
    }

    cancel() {
        this.$uibModalInstance.dismiss('cancel');
    }
}
