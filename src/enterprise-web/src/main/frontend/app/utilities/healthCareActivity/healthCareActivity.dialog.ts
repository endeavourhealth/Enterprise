import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal, NgbTabChangeEvent} from "@ng-bootstrap/ng-bootstrap";
import {HealthCareActivity} from "../models/HealthCareActivity";
import {Msoa} from "../../cohort/models/Msoa";
import {Lsoa} from "../../cohort/models/Lsoa";
import {LoggerService} from "eds-common-js";
import {CohortService} from "../../cohort/cohort.service";
import {UtilitiesService} from "../utilities.service";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";
import {OrganisationGroup} from "../../organisationGroup/models/OrganisationGroup";
import {OrgGroupPickerComponent} from "../../organisationGroup/orgGroupPicker.component";
import {OrganisationGroupService} from "../../organisationGroup/organisationGroup.service";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./healthCareActivity.html')
})
export class HealthCareActivityDialog implements OnInit {

	codeSets:FolderItem[];
	activeTab : string = 'tab-denominator';

	public static open(modalService: NgbModal, healthCareActivity: HealthCareActivity) {
		const modalRef = modalService.open(HealthCareActivityDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.resultData = healthCareActivity;

		return modalRef;
	}

	@Input() resultData;

	population: string = "0";
	codeSet: string = "";
	timePeriodNo: string = "10";
	timePeriod: string = "YEARS";
	title: string = "Health Care Activity";
	postCodePrefix: string = "";
	lsoaCode: Lsoa = <any>[];
	msoaCode: Msoa = <any>[];
	sex: string = "-1";
	ethnicity: string = <any>[];
	orgType: string = "";
	ageFrom: string = "";
	ageTo: string = "";
    dateType: string = "absolute";
    selectedGroupId: number = 1;
    orgGroups: OrganisationGroup[] = [];

	orgTT: string = "To select multiples please use Shift and Click.";
	ppTT: string = "Please select a patient population as the denominator";
	ppTM: string = "Please select the time period over which trend is to be measured. NOTE: Older population counts are skewed due to missing historical data.";
	ppPC: string = "Please select a major post code area";
	ppLSOA: string = "Please select a Lower layer Super Output Area";
	ppMSOA: string = "Please select a Middle layer Super Output Area";
	orgEG: string = "Please select one or more ethnic groups. To select multiple groups please use Shift and Click.";
	absoluteTT: string = "Please choose whether to base the time relative to today's date or using absolute dates (start of year or month)";

	organisations = <any>[];
	msoas = <any>[];
	lsoas = <any>[]
	periods = ['','MONTHS','YEARS'];

	genders = [
		{id: -1, name: 'All'},
		{id: 0, name: 'Males'},
		{id: 1, name: 'Females'}
	];

	organisationTypes = [
		{id: 0, name: 'General Practice'},
		{id: 1, name: 'Acute Trust'},
		{id: 2, name: 'Mental Health'}
	];

	orgTypes = <any>[];

	ethnicGroups = [
		{code: "A", name: 'British'},
		{code: "B", name: 'Irish'},
		{code: "C", name: 'Any other White background'},
		{code: "D", name: 'White and Black Caribbean'},
		{code: "E", name: 'White and Black African'},
		{code: "F", name: 'White and Asian'},
		{code: "G", name: 'Any other mixed background'},
		{code: "H", name: 'Indian'},
		{code: "J", name: 'Pakistani'},
		{code: "K", name: 'Bangladeshi'},
		{code: "L", name: 'Any other Asian background'},
		{code: "M", name: 'Caribbean'},
		{code: "N", name: 'African'},
		{code: "P", name: 'Any other Black background'},
		{code: "R", name: 'Chinese'},
		{code: "S", name: 'Any other ethnic group'},
		{code: "Z", name: 'Not stated'},
	];

	constructor(protected cohortService: CohortService,
							private utilitiesService:UtilitiesService,
							private organisationGroupService: OrganisationGroupService,
							private $modal: NgbModal,
							protected $uibModalInstance : NgbActiveModal,
							private logger : LoggerService) {
	}

	ngOnInit(): void {
		var vm = this;

		vm.cohortService.getLsoaCodes()
			.subscribe(
				(data) => {
					vm.lsoas = data;
				});

		vm.cohortService.getMsoaCodes()
			.subscribe(
				(data) => {
					vm.msoas = data;
				});

		vm.getOrganisationGroups();

	}

    getOrganisationGroups() {
        var vm = this;
        vm.orgGroups = [];
        vm.organisationGroupService.getOrganisationGroups()
            .subscribe(
                (result) => {
                    for (let value of result) {
                        if (value != null) {
                            vm.orgGroups.push({id: value[0], name: value[1]});
                        }
                    }
                });
    }

	setSelectedLsoas(selectElement) {
		var vm = this;
		vm.resultData.lsoaCode = <any>[];
		for (let optionElement of selectElement.selectedOptions) {
			let lsoa = {
				lsoaCode: optionElement.value,
				lsoaName: optionElement.text
			};
			vm.resultData.lsoaCode.push(lsoa);
		}
	}

	setSelectedMsoas(selectElement) {
		var vm = this;
		vm.resultData.msoaCode = <any>[];
		for (let optionElement of selectElement.selectedOptions) {
			let msoa = {
				msoaCode: optionElement.value,
				msoaName: optionElement.text
			};
			vm.resultData.msoaCode.push(msoa);
		}
	}

	setSelectedEthnicGroups(selectElement) {
		var vm = this;
		vm.resultData.ethnicity = <any>[];
		for (let optionElement of selectElement.selectedOptions) {
			vm.resultData.ethnicity.push(optionElement.value);
		}
	}

	run() {
		var vm = this;
		vm.resultData.population = vm.population;
		vm.resultData.timePeriodNo = vm.timePeriodNo;
		vm.resultData.timePeriod = vm.timePeriod;
		vm.resultData.title = vm.title;
		vm.resultData.postCodePrefix = vm.postCodePrefix;
		vm.resultData.sex = vm.sex;
		vm.resultData.ageFrom = vm.ageFrom;
		vm.resultData.ageTo = vm.ageTo;
		vm.resultData.orgType = vm.orgType;
		vm.resultData.dateType = vm.dateType;
		vm.resultData.organisationGroup = vm.selectedGroupId;

		this.ok();
	}

	ok() {
		this.$uibModalInstance.close(this.resultData);
	}

	cancel() {
		this.$uibModalInstance.close(null);
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

    tabChange($event : NgbTabChangeEvent) {
			this.activeTab = $event.nextId;
			console.log(this.activeTab);
		}
}
