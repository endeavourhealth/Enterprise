import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {LoggerService, MessageBoxDialog} from "eds-common-js";
import {UtilitiesService} from "./utilities.service";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {OrganisationGroup} from "./models/OrganisationGroup";
import {Organisation} from "../report/models/Organisation";

@Component({
    selector: 'ngbd-modal-content',
    template: require('./orgGroupPicker.html')
})
export class OrgGroupPickerComponent implements OnInit {

    codeSets:FolderItem[];

    public static open(modalService: NgbModal, groupId: number) {
        const modalRef = modalService.open(OrgGroupPickerComponent, { backdrop : "static", size : "lg"});
        modalRef.componentInstance.groupId = groupId;

        return modalRef;
    }

    @Input() groupId: number;
    orgGroups : OrganisationGroup[] = [];
    availableOrganisations: Organisation[] = [];
    selectedGroup: OrganisationGroup = {id: 0, name: ""};


    constructor(private utilitiesService:UtilitiesService,
                private $modal: NgbModal,
                protected $uibModalInstance : NgbActiveModal,
                private logger : LoggerService) {
    }

    ngOnInit(): void {
        var vm = this;
        vm.getOrganisationGroups();
        vm.getAvailableOrganisations();
    }

    getOrganisationGroups() {
        var vm = this;
        vm.orgGroups = [];
        vm.utilitiesService.getOrganisationGroups()
            .subscribe(
                (result) => {
                    for (let value of result) {
                        if (value != null) {
                            let group: OrganisationGroup = {id: value[0], name: value[1]};
                            vm.orgGroups.push(group);
                            if (group.id === vm.groupId) {
                                vm.selectedGroup = group;
                            }
                        }
                    }
                    vm.getOrganisationsInGroup();
                });
    }

    getOrganisationsInGroup() {
        var vm = this;
        vm.selectedGroup.organisations = [];
        vm.utilitiesService.getOrganisationsInGroup(vm.selectedGroup.id)
            .subscribe(
                (result) => {
                    for (let value of result)
                        if (value != null)
                            vm.selectedGroup.organisations.push({id: value[0], name: value[1]});
                });
    }

    getAvailableOrganisations() {
        var vm = this;
        vm.utilitiesService.getAvailableOrganisation()
            .subscribe(
                (result) => {
                    for (let value of result)
                        if (value != null) {
                            vm.availableOrganisations.push({id: value[1], name: value[0]});
                        }
                });
    }

    groupChanged() {
        var vm = this;
        this.getOrganisationsInGroup();
    }

    private addToSelection(match: Organisation) {
        if (!this.selectedGroup.organisations.some(x => x.id === match.id)) {
            this.selectedGroup.organisations.push(match);
        }
    }

    private removeFromSelection(match: Organisation) {
        const index = this.selectedGroup.organisations.indexOf(match, 0);
        if (index > -1) {
            this.selectedGroup.organisations.splice(index, 1);
        }
    }

    ok() {
        var vm = this;
        this.$uibModalInstance.close(vm.selectedGroup.id);
    }

    cancel() {
        this.$uibModalInstance.close(null);
    }

    save(close: boolean = false) {
        var vm = this;
        vm.utilitiesService.updateOrganisationGroup(vm.selectedGroup)
            .subscribe(
                (result) => {
                    console.log('saved');
                    vm.groupId = result;
                    vm.selectedGroup.id = result;
                    if (!close) {
                        vm.getOrganisationGroups();
                    } else {
                        this.$uibModalInstance.close(this.selectedGroup.id);
                    }
                });

    }

    saveClose() {
        var vm = this;
        vm.save(true)
    }

    addNewGroup() {
        var vm = this;

        let newGroup: OrganisationGroup = {id: 0, name: ""};
        vm.orgGroups.push(newGroup);
        vm.selectedGroup = newGroup;
        vm.selectedGroup.organisations = [];
    }
}
