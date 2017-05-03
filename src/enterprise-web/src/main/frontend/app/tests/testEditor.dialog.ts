import {OnInit, Input, Component} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

import {CodePickerDialog} from "../coding/codePicker.dialog";
import {Test} from "./models/Test";
import {CodeSetValue} from "../codeSet/models/CodeSetValue";
import {Restriction} from "../expressions/models/Restriction";
import {ValueTo} from "./models/ValueTo";
import {Filter} from "./models/Filter";
import {CodeSet} from "../codeSet/models/CodeSet";
import {ValueFrom} from "./models/ValueFrom";
import {ValueSet} from "./models/ValueSet";
import {CodingService} from "../coding/coding.service";
import {QueryPickerDialog} from "../query/queryPicker.dialog";
import {QuerySelection} from "../query/models/QuerySelection";
import {EnterpriseLibraryItem} from "../enterpriseLibrary/models/EnterpriseLibraryItem";
import {LibraryService, LoggerService} from "eds-common-js";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./testEditor.html'),
	entryComponents : [
		QueryPickerDialog
	]
})
export class TestEditDialog implements OnInit{
	public static open(modalService: NgbModal, test : Test, type : any, rules : any) {
		const modalRef = modalService.open(TestEditDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.resultData = test;
		modalRef.componentInstance.type = type;
		modalRef.componentInstance.rules = rules;

		return modalRef;
	}

	@Input() resultData : any;
	@Input() type : any;
	@Input() rules = <any>[];

	showConceptResults : boolean = false;
	title : string;

	testType : string = "";
	testRuleId : string;
	testValueFrom : string;
	testValueTo : string;
	testDateFromRuleId : string;
	testDateToRuleId : string;

	dateEditor : boolean = false;
	problemEditor : boolean = false;
	problem : boolean;
	activeEditor : boolean = false;
	active : boolean;
	authTypeEditor : boolean = false;
	acute : boolean;
	repeat : boolean;
	repeatDispensing : boolean;
	automatic : boolean;
	datetype: String;
	filterDateFrom : String;
	filterDateTo : String;
	filterDateFromRelativeValue : String;
	filterDateToRelativeValue : String;
	filterDateFromRelativePeriod : String;
	filterDateToRelativePeriod : String;
	restriction: string;
	restrictionCount: string = "1";
	fieldPrefix: string;
	editMode : boolean = false;
	showRestriction : boolean = false;
	codeSelection : CodeSetValue[] = [];
	fields = <any>[];

	restrictions = ['','ALL','LATEST','EARLIEST','HIGHEST','LOWEST'];
	periods = ['','DAY','WEEK','MONTH','YEAR'];
	keepFields = ['CLINICAL_DATE','VALUE','CODE','HCP'];

	libraryItem : EnterpriseLibraryItem;

	constructor(
		protected $modal : NgbModal,
		protected activeModal : NgbActiveModal,
		private codingService : CodingService,
		protected libraryService : LibraryService,
		protected logger : LoggerService) {
	}

	ngOnInit(): void {

		var newTest : Test = {
			testRuleId: "",
			filter: [],
			restriction: null
		};

		let rule = {
			value: "0",
			displayName: "Baseline Date"
		};
		this.rules.push(rule);

		if (!this.resultData||!this.resultData.filter)
			this.resultData = newTest;
		else
			this.initialiseEditMode(this.resultData);

		if (this.type=="1")
			this.title = "Feature Editor";
		else if (this.type=="3")
			this.title = "Test Editor";


	}

	initialiseEditMode(resultData : Test) {
		var vm = this;

		vm.editMode = true;

		if (vm.type=="3")
			vm.testRuleId = vm.resultData.testRuleId;

		if (resultData.filter === null) {
			resultData.filter = [];
		}

		for (var f = 0; f < resultData.filter.length; ++f) {
			var filter = resultData.filter[f];
			var field = filter.field;

			switch(field) {
				case "CONCEPT":
					vm.codeSelection = filter.codeSet.codeSetValue;
					vm.showConceptResults = true;
					if (filter.codeSet.codeSetValue[0].baseType=="Observation"||
						filter.codeSet.codeSetValue[0].baseType=="Medication Statement"||
						filter.codeSet.codeSetValue[0].baseType=="Medication Order"||
						filter.codeSet.codeSetValue[0].baseType=="Referral"||
						filter.codeSet.codeSetValue[0].baseType=="Allergy"||
						filter.codeSet.codeSetValue[0].baseType=="Encounter")
						vm.dateEditor = true;
					if (filter.codeSet.codeSetValue[0].baseType=="Observation")
						vm.problemEditor = true;
					if (filter.codeSet.codeSetValue[0].baseType=="Medication Statement") {
						vm.activeEditor = true;
						vm.authTypeEditor = true;
					}
					if (vm.type=="3")
						vm.testType="concept";
					break;
				case "EFFECTIVE_DATE":
					if (filter.valueFrom) {
						if (filter.valueFrom.absoluteUnit) {
							vm.filterDateFrom = filter.valueFrom.constant;
							vm.datetype = "absolute";
						}
						else if (filter.valueFrom.relativeUnit) {
							vm.filterDateFromRelativeValue = filter.valueFrom.constant;
							vm.filterDateFromRelativePeriod = filter.valueFrom.relativeUnit;
							vm.datetype = "relative";
							vm.testDateFromRuleId = filter.valueFrom.testRuleId;
						}
					}
					if (filter.valueTo) {
						if (filter.valueTo.absoluteUnit) {
							vm.filterDateTo = filter.valueTo.constant;
							vm.datetype = "absolute";
						}
						else if (filter.valueTo.relativeUnit) {
							vm.filterDateToRelativeValue = filter.valueTo.constant;
							vm.filterDateToRelativePeriod = filter.valueTo.relativeUnit;
							vm.datetype = "relative";
							vm.testDateToRuleId = filter.valueTo.testRuleId;
						}
					}
					if (vm.type=="3")
						vm.testType="date";
					break;
				case "VALUE":
					if (filter.valueFrom)
						vm.testValueFrom = filter.valueFrom.constant;
					if (filter.valueTo)
						vm.testValueTo = filter.valueTo.constant;
					if (vm.type=="3")
						vm.testType="value";
					break;
				case "OBSERVATION_PROBLEM":
					for (var i = 0, len = filter.valueSet.value.length; i < len; i++) {
						if (filter.valueSet.value[i]=="PROBLEM")
							vm.problem = true;
					}
					break;
				case "MEDICATION_STATUS":
					for (var i = 0, len = filter.valueSet.value.length; i < len; i++) {
						if (filter.valueSet.value[i]=="ACTIVE")
							vm.active = true;
					}
					break;
				case "MEDICATION_TYPE":
					for (var i = 0, len = filter.valueSet.value.length; i < len; i++) {
						if (filter.valueSet.value[i]=="ACUTE")
							vm.acute = true;
						else if (filter.valueSet.value[i]=="REPEAT")
							vm.repeat = true;
						else if (filter.valueSet.value[i]=="REPEAT_DISPENSING")
							vm.repeatDispensing = true;
						else if (filter.valueSet.value[i]=="AUTOMATIC")
							vm.automatic = true;
					}
					break;
			}

		}

		if (resultData.restriction) {
			vm.showRestriction = true;
			vm.restriction = resultData.restriction.restriction;
			vm.restrictionCount = resultData.restriction.count;
			vm.fieldPrefix = resultData.restriction.prefix;
		}

	}

	getRuleName(ruleId : any) {
		var vm = this;
		for (var i = 0, len = vm.rules.length; i < len; i++) {
			if (vm.rules[i].value == ruleId) {
				return vm.rules[i].displayName;
			}
		}
		return null;
	}

	formatDate(inputDate : Date) {
		return this.zeroFill(inputDate.getDate(),2)  + "-" + this.zeroFill((inputDate.getMonth()+1),2) + "-" + inputDate.getFullYear();
	}

	showCodeSetPicker() {
		let querySelection: QuerySelection;
		let vm = this;
		QueryPickerDialog.open(this.$modal, querySelection)
			.result.then(function (resultData: QuerySelection) {

			vm.libraryService.getLibraryItem<EnterpriseLibraryItem>(resultData.id)
				.subscribe(
					(libraryItem) => {
						vm.libraryItem = libraryItem;

						if (vm.codeSelection.length>0) {
							for (var i = 0; i < vm.resultData.filter.length; ++i) {
								var flt = vm.resultData.filter[i];

								if (flt.field=="CONCEPT")
									vm.resultData.filter.splice(i, 1);
							}
						}

						vm.codeSelection = libraryItem.codeSet.codeSetValue;

						var codeSet : CodeSet = {
							codingSystem : "ENDEAVOUR",
							codeSetValue : libraryItem.codeSet.codeSetValue
						}

						var filter: Filter = {
							field: "CONCEPT",
							valueFrom: null,
							valueTo: null,
							codeSet: null,
							valueSet: null,
							codeSetLibraryItemUuid: null,
							negate: false
						};

						filter.codeSet = codeSet;

						vm.resultData.filter.push(filter);

						vm.showConceptResults = true;

						if (libraryItem.codeSet.codeSetValue[0].baseType=="Observation"||
							libraryItem.codeSet.codeSetValue[0].baseType=="Medication Statement"||
							libraryItem.codeSet.codeSetValue[0].baseType=="Medication Order"||
							libraryItem.codeSet.codeSetValue[0].baseType=="Referral"||
							libraryItem.codeSet.codeSetValue[0].baseType=="Allergy"||
							libraryItem.codeSet.codeSetValue[0].baseType=="Encounter")
							vm.dateEditor = true;

						if (libraryItem.codeSet.codeSetValue[0].baseType=="Observation")
							vm.problemEditor = true;


						if (libraryItem.codeSet.codeSetValue[0].baseType=="Medication Statement") {
							vm.activeEditor = true;
							vm.authTypeEditor = true;
						}
					}

				);



		});
	}

	showCodePicker() {
		var vm = this;

		CodePickerDialog.open(this.$modal, vm.codeSelection)
			.result.then(function(resultData : CodeSetValue[]){

			if (vm.codeSelection.length>0) {
				for (var i = 0; i < vm.resultData.filter.length; ++i) {
					var flt = vm.resultData.filter[i];

					if (flt.field=="CONCEPT")
						vm.resultData.filter.splice(i, 1);
				}
			}

			vm.codeSelection = resultData;

			if (resultData.length==0) {
				return;
			}

			var codeSet : CodeSet = {
				codingSystem : "ENDEAVOUR",
				codeSetValue : resultData
			}

			var filter: Filter = {
				field: "CONCEPT",
				valueFrom: null,
				valueTo: null,
				codeSet: null,
				valueSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			filter.codeSet = codeSet;

			vm.resultData.filter.push(filter);

			vm.showConceptResults = true;

			if (resultData[0].baseType=="Observation"||
				resultData[0].baseType=="Medication Statement"||
				resultData[0].baseType=="Medication Order"||
				resultData[0].baseType=="Referral"||
				resultData[0].baseType=="Allergy"||
				resultData[0].baseType=="Encounter")
				vm.dateEditor = true;

			if (resultData[0].baseType=="Observation")
				vm.problemEditor = true;

			if (resultData[0].baseType=="Medication Statement") {
				vm.activeEditor = true;
				vm.authTypeEditor = true;
			}

		});
	}

	removeFilter(filter: any) {
		var vm = this;

		for (var i = vm.resultData.filter.length-1; i >= 0; --i) {
			var f = vm.resultData.filter[i];

			switch(filter) {
				case "date":
					if (f.field=="EFFECTIVE_DATE") {
						vm.testType = "";
						vm.dateEditor = false;
						vm.resultData.filter.splice(i, 1);
						vm.datetype = "";
					}
					break;
				case "value":
					if (f.field=="VALUE") {
						vm.testType = "";
						vm.resultData.filter.splice(i, 1);
					}
					break;
				case "restriction":
					vm.showRestriction = false;
					vm.resultData.restriction = null;
					break;
			}
		}
	}

	zeroFill( number : any, width : any ) {
		width -= number.toString().length;
		if ( width > 0 )
		{
			return new Array( width + (/\./.test( number ) ? 2 : 1) ).join( '0' ) + number;
		}
		return number + ""; // always return a string
	}

	testValueFromChange(value : any) {
		var vm = this;

		if (!value)
			value="";

		var valueFrom : ValueFrom = {
			constant: value,
			testRuleId : "",
			absoluteUnit: "NUMERIC",
			relativeUnit: null,
			operator: "GREATER_THAN_OR_EQUAL_TO"
		}

		var filter: Filter = {
			field: "VALUE",
			valueFrom: valueFrom,
			valueTo: null,
			codeSet: null,
			valueSet: null,
			codeSetLibraryItemUuid: null,
			negate: false
		};

		var foundEntry : boolean = false;

		for (var i = 0; i < vm.resultData.filter.length; ++i) {
			var flt = vm.resultData.filter[i];

			if (flt.field=="VALUE" && flt.valueFrom && value!="") {
				foundEntry = true;
				filter.valueFrom = valueFrom;
				break;
			}
			else if (flt.field=="VALUE" && flt.valueFrom && value=="")
				vm.resultData.filter.splice(i, 1);
		}

		if (!foundEntry && value!="")
			vm.resultData.filter.push(filter);
	}

	testValueToChange(value : any) {
		var vm = this;

		if (!value)
			value="";

		var valueTo : ValueTo = {
			constant: value,
			testRuleId : "",
			absoluteUnit: "NUMERIC",
			relativeUnit: null,
			operator: "LESS_THAN_OR_EQUAL_TO"
		}

		var filter: Filter = {
			field: "VALUE",
			valueFrom: null,
			valueTo: valueTo,
			codeSet: null,
			valueSet: null,
			codeSetLibraryItemUuid: null,
			negate: false
		};

		var foundEntry : boolean = false;

		for (var i = 0; i < vm.resultData.filter.length; ++i) {
			var flt = vm.resultData.filter[i];

			if (flt.field=="VALUE" && flt.valueTo && value!="") {
				foundEntry = true;
				filter.valueTo = valueTo;
				break;
			}
			else if (flt.field=="VALUE" && flt.valueTo && value=="")
				vm.resultData.filter.splice(i, 1);
		}

		if (!foundEntry && value!="")
			vm.resultData.filter.push(filter);

	}

	filterDateFromChange(value : any) {
		var vm = this;

		if (!value)
			value="";

		var datestring : string = value;
		var dateField : string = "EFFECTIVE_DATE";

		var valueFrom : ValueFrom = {
			constant: datestring,
			testRuleId : "",
			absoluteUnit: "DATE",
			relativeUnit: null,
			operator: "GREATER_THAN_OR_EQUAL_TO"
		}

		var filter: Filter = {
			field: dateField,
			valueFrom: valueFrom,
			valueTo: null,
			codeSet: null,
			valueSet: null,
			codeSetLibraryItemUuid: null,
			negate: false
		};

		for (var i = 0; i < vm.resultData.filter.length; ++i) {
			var flt = vm.resultData.filter[i];

			if (flt.field==dateField && flt.valueFrom)
				vm.resultData.filter.splice(i, 1);
		}

		if (value!="") {
			vm.resultData.filter.push(filter);
		}

	}

	filterRelativeDateFromChange(value : any, period : any, testDateFromRuleId : any) {
		var vm = this;

		if (!value)
			value="";

		var dateField : string = "EFFECTIVE_DATE";

		var valueFrom : ValueFrom = {
			constant: value,
			testRuleId : testDateFromRuleId,
			absoluteUnit: null,
			relativeUnit: period,
			operator: "GREATER_THAN_OR_EQUAL_TO"
		}

		var filter: Filter = {
			field: dateField,
			valueFrom: valueFrom,
			valueTo: null,
			codeSet: null,
			valueSet: null,
			codeSetLibraryItemUuid: null,
			negate: false
		};

		for (var i = 0; i < vm.resultData.filter.length; ++i) {
			var flt = vm.resultData.filter[i];

			if (flt.field==dateField && flt.valueFrom)
				vm.resultData.filter.splice(i, 1);
		}

		if (value!="") {
			vm.resultData.filter.push(filter);
		}

	}

	filterDateToChange(value : any) {
		var vm = this;

		if (!value)
			value="";

		var datestring : string = value;

		var dateField : string = "EFFECTIVE_DATE";

		var valueTo : ValueTo = {
			constant: datestring,
			testRuleId : "",
			absoluteUnit: "DATE",
			relativeUnit: null,
			operator: "LESS_THAN_OR_EQUAL_TO"
		}

		var filter: Filter = {
			field: dateField,
			valueFrom: null,
			valueTo: valueTo,
			codeSet: null,
			valueSet: null,
			codeSetLibraryItemUuid: null,
			negate: false
		};

		for (var i = 0; i < vm.resultData.filter.length; ++i) {
			var flt = vm.resultData.filter[i];

			if (flt.field==dateField && flt.valueTo)
				vm.resultData.filter.splice(i, 1);
		}

		if (value!="") {
			vm.resultData.filter.push(filter);
		}

	}

	filterRelativeDateToChange(value : any, period : any, testDateToRuleId : any) {
		var vm = this;

		if (!value)
			value="";

		var dateField : string = "EFFECTIVE_DATE";

		var valueTo : ValueTo = {
			constant: value,
			testRuleId : testDateToRuleId,
			absoluteUnit: null,
			relativeUnit: period,
			operator: "LESS_THAN_OR_EQUAL_TO"
		}

		var filter: Filter = {
			field: dateField,
			valueFrom: null,
			valueTo: valueTo,
			codeSet: null,
			valueSet: null,
			codeSetLibraryItemUuid: null,
			negate: false
		};

		for (var i = 0; i < vm.resultData.filter.length; ++i) {
			var flt = vm.resultData.filter[i];

			if (flt.field==dateField && flt.valueTo)
				vm.resultData.filter.splice(i, 1);
		}

		if (value!="") {
			vm.resultData.filter.push(filter);
		}

	}

	problemChange(e) {
		var vm = this;

		vm.problem = e.target.checked;

	}

	activeChange(e) {
		var vm = this;

		vm.active = e.target.checked;
	}

	acuteChange(e) {
		var vm = this;

		vm.acute = e.target.checked;
	}

	repeatChange(e) {
		var vm = this;

		vm.repeat = e.target.checked;
	}

	repeatDispensingChange(e) {
		var vm = this;

		vm.repeatDispensing = e.target.checked;
	}

	automaticChange(e) {
		var vm = this;

		vm.automatic = e.target.checked;
	}

	toggleRestriction() {
		var vm = this;

		vm.showRestriction = !vm.showRestriction;
	};

	save() {
		var vm = this;

		if (vm.datetype == 'absolute') {
			vm.filterDateFromChange(vm.filterDateFrom);
			vm.filterDateToChange(vm.filterDateTo);
		} else if (vm.datetype == 'relative') {
			vm.filterRelativeDateFromChange(vm.filterDateFromRelativeValue, vm.filterDateFromRelativePeriod, vm.testDateFromRuleId)
			vm.filterRelativeDateToChange(vm.filterDateToRelativeValue, vm.filterDateToRelativePeriod, vm.testDateToRuleId)
		}

		console.log(vm.resultData);

		vm.removeAttributes("OBSERVATION_PROBLEM");
		vm.removeAttributes("MEDICATION_STATUS");
		vm.removeAttributes("MEDICATION_TYPE");

		if (vm.problem)
			vm.filterValueChange(["PROBLEM"],"OBSERVATION_PROBLEM");

		if (vm.active)
			vm.filterValueChange(["ACTIVE"],"MEDICATION_STATUS");

		let medTypes = <any>[];

		if (vm.acute)
			medTypes.push("ACUTE");
		if (vm.repeat)
			medTypes.push("REPEAT");
		if (vm.repeatDispensing)
			medTypes.push("REPEAT_DISPENSING");
		if (vm.automatic)
			medTypes.push("AUTOMATIC");

		if (medTypes.length>0)
			vm.filterValueChange(medTypes,"MEDICATION_TYPE");

		if (vm.type=="3")
			vm.resultData.testRuleId = vm.testRuleId;
		else
			vm.resultData.testRuleId = "";

		this.ok();
	}

	removeAttributes(filter: any) {
		var vm = this;

		for (var i = vm.resultData.filter.length-1; i >= 0; --i) {
			var f = vm.resultData.filter[i];

			if (f.field==filter) {
				vm.resultData.filter.splice(i, 1);
			}
		}
	}

	filterValueChange(values : any[], valueField : any) {
		var vm = this;

		var valueSet : ValueSet = {
			value: []
		}

		var value = "";

		for (var i = values.length-1; i >= 0; --i) {
			value = values[i];
			valueSet.value.push(value);
		}

		var filter : Filter = {
			field: valueField,
			valueFrom: null,
			valueTo: null,
			codeSet: null,
			valueSet: valueSet,
			codeSetLibraryItemUuid: null,
			negate: false
		};

		var foundEntry : boolean = false;

		for (var i = 0; i < vm.resultData.filter.length; ++i) {
			var flt = vm.resultData.filter[i];

			if (flt.field==valueField && flt.valueSet && value!="") {
				foundEntry = true;
				filter.valueSet = valueSet;
				break;
			}
			else if (flt.field==valueField && flt.valueSet && value=="")
				vm.resultData.filter.splice(i, 1);
		}

		if (!foundEntry && value!="")
			vm.resultData.filter.push(filter);
	}

	setSelectedFields(selectElement) {
		var vm = this;
		vm.fields = <any>[];
		for (var i = 0; i < selectElement.options.length; i++) {
			var optionElement = selectElement.options[i];
			if (optionElement.selected) {
				vm.fields.push(optionElement.value);
			}
		}
		this.restrictionChange('1');
	}

	restrictionChange(value : any) {
		var vm = this;

		if (!value || vm.restriction=="") {
			vm.resultData.restriction = null;
			return;
		}

		var restriction : Restriction = {
			restriction: vm.restriction,
			count: vm.restrictionCount,
			prefix: vm.fieldPrefix,
			field: vm.fields
		};

		vm.resultData.restriction = restriction;

		console.log(vm.resultData.restriction);
	}

	ok() {
		this.activeModal.close(this.resultData);
	}

	cancel() {
		this.activeModal.dismiss('cancel');
	}
}
