import {Component, Input, OnInit} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {PrevInc} from "./models/PrevInc";
import {Organisation} from "../report/models/Organisation";
import {Msoa} from "../cohort/models/Msoa";
import {Lsoa} from "../cohort/models/Lsoa";
import {LoggerService, MessageBoxDialog} from "eds-common-js";
import {CohortService} from "../cohort/cohort.service";
import {UtilitiesService} from "./utilities.service";
import {FolderItem} from "eds-common-js/dist/folder/models/FolderItem";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./prevInc.html')
})
export class PrevIncDialog implements OnInit {

	codeSets:FolderItem[];

	public static open(modalService: NgbModal, prevInc: PrevInc) {
		const modalRef = modalService.open(PrevIncDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.resultData = prevInc;

		return modalRef;
	}

	@Input() resultData;

	organisation: Organisation = <any>[];
	population: string = "0";
	codeSet: string = "";
	timePeriodNo: string = "10";
	timePeriod: string = "YEARS";
	title: string = "Incidence and Prevalence";
	diseaseCategory: string = "0";
	postCodePrefix: string = "";
	lsoaCode: Lsoa = <any>[];
	msoaCode: Msoa = <any>[];
	sex: string = "-1";
	ethnicity: string = <any>[];
	orgType: string = "";
	ageFrom: string = "";
	ageTo: string = "";

	orgTT: string = "To select multiples please use Shift and Click.";
	ppTT: string = "Please select a patient population as the denominator";
	ppTM: string = "Please select the time period over which prevalence and incidence trend is to be measured. NOTE: Older population counts are skewed due to missing historical data.";
	ppCT: string = "In chronic disease incidence is measured as the “first occurrence” of that disease in the record. "+
	"In acute disease, prevalence cannot be practically measured because illnesses are short lived and their end date is not recorded. Incidence is measured by the presence of an entry that is not marked as a review/end.";
	ppPC: string = "Please select a major post code area";
	ppLSOA: string = "Please select a Lower layer Super Output Area";
	ppMSOA: string = "Please select a Middle layer Super Output Area";
	orgEG: string = "Please select one or more ethnic groups. To select multiple groups please use Shift and Click.";

	organisations = <any>[];
	msoas = <any>[];
	lsoas = <any>[]
	periods = ['','MONTHS','YEARS'];

	diseaseCategories = [
		{id: -1, name: ''},
		{id: 0, name: 'Chronic Disease'},
		{id: 1, name: 'Acute Disease'}
	];

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

		vm.getCodeSets();

		vm.cohortService.getRegions()
			.subscribe(
				(data) => {
					vm.orgTypes = data;
					let t = {uuid: '1', name: 'Choose Organisations'};
					vm.orgTypes.unshift(t);
					t = {uuid: '0', name: 'Organisation Types'};
					vm.orgTypes.unshift(t);
				});
	}

	getCodeSets() {
		var vm = this;

		vm.codeSets = null;
		vm.utilitiesService.getCodeSets()
			.subscribe(
				(data:FolderItem[]) => vm.codeSets = data
			);
	}

	setSelectedOrganisations(selectElement) {
		var vm = this;
		vm.resultData.organisation = <any>[];
		for (let optionElement of selectElement.selectedOptions) {
			let odscode = optionElement.value.split('~')[1];
			let org = {
				id: optionElement.value.split('~')[0],
				name: optionElement.text,
				odsCode: odscode
			};
			vm.cohortService.getOrgsForParentOdsCode(odscode)
				.subscribe(
					(data) => {
						let orgs = <any>[];
						orgs = data;
						for (let org of orgs) {
							let o = {
								id: org.id,
								name: org.name,
								odsCode: org.odsCode
							};
							vm.resultData.organisation.push(o);
						}
					});
			vm.resultData.organisation.push(org);
		}
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

	setSelectedOrgType(selectElement) {
		var vm = this;
		if (vm.orgType === "0") {
			vm.organisations = vm.organisationTypes;
		} else if (vm.orgType === "1") {
			vm.cohortService.getOrganisations()
				.subscribe(
					(data) => {
						vm.organisations = data;
					});
		} else {
			vm.cohortService.getOrgsForRegion(vm.orgType)
				.subscribe(
					(data) => {
						vm.organisations = data;
					});
		}
	}

	run() {
		var vm = this;
		vm.resultData.population = vm.population;
		vm.resultData.codeSet = vm.codeSet;
		vm.resultData.timePeriodNo = vm.timePeriodNo;
		vm.resultData.timePeriod = vm.timePeriod;
		vm.resultData.title = vm.title;
		vm.resultData.diseaseCategory = vm.diseaseCategory;
		vm.resultData.postCodePrefix = vm.postCodePrefix;
		vm.resultData.sex = vm.sex;
		vm.resultData.ageFrom = vm.ageFrom;
		vm.resultData.ageTo = vm.ageTo;
		vm.resultData.orgType = vm.orgType;

		this.ok();
	}

	ok() {
		this.$uibModalInstance.close(this.resultData);
	}

	cancel() {
		this.$uibModalInstance.close(null);
	}
}
