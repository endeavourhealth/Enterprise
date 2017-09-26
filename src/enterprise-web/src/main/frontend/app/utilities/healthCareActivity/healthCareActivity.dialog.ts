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
import {ITreeOptions} from "angular2-tree-component";

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

	options : ITreeOptions;
	encounterTreeData : any[];

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
	encounterType: string = <any>[];
	orgType: string = "";
	ageFrom: string = "";
	ageTo: string = "";
    dateType: string = "absolute";
    selectedGroupId: number = 0;
	selectedServiceGroupId: number = 0;
    orgGroups: OrganisationGroup[] = [];
	serviceGroups: OrganisationGroup[] = [];

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

		this.options = {
			childrenField : 'children',
			idField : 'id'
		};
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

		// temp data in JSON until IM Concept table created in MySQL - awaiting full mapping from source
		vm.encounterTreeData = [
			{
				id: '0',
				name: 'All',
				checked: false,
				children: [
					{ id: '8000', name: 'Encounters by setting', checked: false,
						children: [
							{ id: '113', name: 'Provided in service setting', checked: false,
								children: [
									{ id: '8003', name: 'General Practice consultation', checked: false },
									{ id: '8005', name: 'Acute care setting', checked: false,
										children: [
											{ id: '8016', name: 'Accident and emergency attendance', checked: false }
										]
									},
									{ id: '8006', name: 'Community health setting', checked: false },
									{ id: '8007', name: 'Social care settings', checked: false }
								]
							}
						]
					},
					{ id: '8001', name: 'Encounters by interaction mode', checked: false,
						children: [
							{ id: '8008', name: 'Face to face encounter', checked: false },
							{ id: '8009', name: 'E-mail encounter', checked: false },
							{ id: '8010', name: 'Video encounter', checked: false }
						]
					},
					{ id: '8002', name: 'Encounters by care process', checked: false,
						children: [
							{ id: '8014', name: 'Hospital admission', checked: false },
							{ id: '8015', name: 'Hospital Discharge', checked: false }
						]
					},
					{ id: '8011', name: 'Encounter by place type', checked: false },
					{ id: '8013', name: 'Administration entry', checked: false }
				]
			}
		];

	}

	check(node, checked) {
		this.updateChildNodeCheckbox(node, checked);
		this.updateParentNodeCheckbox(node.realParent);
	}

	updateChildNodeCheckbox(node, checked) {
		node.data.checked = checked;
		if (node.children) {
			node.children.forEach((child) => this.updateChildNodeCheckbox(child, checked));
		}
	}

	updateParentNodeCheckbox(node) {
		if (!node) {
			return;
		}

		let allChildrenChecked = true;
		let noChildChecked = true;

		for (const child of node.children) {
			if (!child.data.checked || child.data.indeterminate) {
				allChildrenChecked = false;
			}
			if (child.data.checked) {
				noChildChecked = false;
			}
		}

		if (allChildrenChecked) {
			node.data.checked = true;
			node.data.indeterminate = false;
		} else if (noChildChecked) {
			node.data.checked = false;
			node.data.indeterminate = false;
		} else {
			node.data.checked = false;
			node.data.indeterminate = true;
		}
		this.updateParentNodeCheckbox(node.parent);
	}

    getOrganisationGroups() {
        var vm = this;
        vm.orgGroups = [];
		vm.serviceGroups = [];
        vm.organisationGroupService.getOrganisationGroups()
            .subscribe(
                (result) => {
                    for (let value of result) {
                        if (value != null) {
                            vm.orgGroups.push({id: value[0], name: value[1]});
							vm.serviceGroups.push({id: value[0], name: value[1]});
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

	checkEncounterSelection() {
		var vm = this;

		for (let node of vm.encounterTreeData) {
			if (node.checked) {
				vm.resultData.encounterType.push('0');
				break;
			}
			if (node.children)
				vm.checkEncounterChildSelection(node.children);
		}

	}

	checkEncounterChildSelection(children) {
		var vm = this;

		for (let node of children) {
			if (node.checked) {
				vm.resultData.encounterType.push(node.id);
			}
			if (node.children)
				vm.checkEncounterChildSelection(node.children);
		}

	}

	run() {
		var vm = this;

		vm.checkEncounterSelection();

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
		vm.resultData.serviceGroupId = vm.selectedServiceGroupId;

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

	serviceManager() {
		var vm = this;
		OrgGroupPickerComponent.open(this.$modal, vm.selectedServiceGroupId ).result.then(
			(result) => {
				if (result) {
					vm.selectedServiceGroupId = result;
					vm.getOrganisationGroups();
					console.log(vm.selectedServiceGroupId);
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
