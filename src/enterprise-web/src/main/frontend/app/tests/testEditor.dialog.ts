import {OnInit, Input, Component} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

import {CodePickerDialog} from "../coding/codePicker.dialog";
import {TermPickerDialog} from "../coding/termPicker.dialog";
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
	public static open(modalService: NgbModal, test : Test, type : any, restrictions : any) {
		const modalRef = modalService.open(TestEditDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.resultData = test;
		modalRef.componentInstance.type = type;
		modalRef.componentInstance.restrictions = restrictions;

		return modalRef;
	}

	@Input() resultData : any;
	@Input() type : any;
	@Input() restrictions = <any>[];

	showConceptResults : boolean = false;
	title : string;

	filterValueFrom : string;
	filterValueTo : string;
	dateEditor : boolean = false;
	problemEditor : boolean = false;
	problem : boolean = false;
	activeEditor : boolean = false;
	active : boolean;
	authTypeEditor : boolean = false;
	acute : boolean;
	repeat : boolean;
	repeatDispensing : boolean;
	automatic : boolean;
	datetype: String = '';
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
	codeCompareSelection : CodeSetValue[] = [];
	fields = <any>[];

	restrictionTypes = ['','ALL','LATEST','EARLIEST','HIGHEST','LOWEST'];
	periods = ['','DAY','WEEK','MONTH','YEAR'];
	keepFields = ['CLINICAL_DATE','VALUE','CODE'];

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
			filter: [],
			restriction: null
		};

		if (!this.resultData||!this.resultData.filter)
			this.resultData = newTest;
		else
			this.initialiseEditMode(this.resultData);

		if (this.type=="1")
			this.title = "Feature";
		else if (this.type=="3")
			this.title = "Test";

	}

	initialiseEditMode(resultData : Test) {
		var vm = this;

		vm.editMode = true;

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
						}
					}
					break;
				case "VALUE":
					if (filter.valueFrom)
						vm.filterValueFrom = filter.valueFrom.constant;
					if (filter.valueTo)
						vm.filterValueTo = filter.valueTo.constant;
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
				default :
					if (vm.type=="3") {
						if (filter.valueFrom) {
							for (var i = vm.restrictions.length-1; i >= 0; --i) {
								if (field==vm.restrictions[i].field) {
									vm.restrictions[i].filter.valueFrom.constant = filter.valueFrom.constant;
									if (filter.valueFrom.relativeUnit) {
										vm.restrictions[i].filter.valueFrom.relativeUnit = filter.valueFrom.relativeUnit;
										vm.restrictions[i].filter.valueFrom.testField = filter.valueFrom.testField;
									}
								}
							}
						}
						if (filter.valueTo) {
							for (var i = vm.restrictions.length-1; i >= 0; --i) {
								if (field==vm.restrictions[i].field) {
									vm.restrictions[i].filter.valueTo.constant = filter.valueTo.constant;
									if (filter.valueTo.relativeUnit) {
										vm.restrictions[i].filter.valueTo.relativeUnit = filter.valueTo.relativeUnit;
										vm.restrictions[i].filter.valueTo.testField = filter.valueTo.testField;
									}
								}
							}
						}
						if (filter.codeSet) {
							for (var i = vm.restrictions.length-1; i >= 0; --i) {
								if (field==vm.restrictions[i].field) {
									vm.restrictions[i].filter.codeSet = filter.codeSet;
								}
							}
						}
					}
					break;
			}

		}

		if (resultData.restriction) {
			vm.showRestriction = true;
			vm.restriction = resultData.restriction.restriction;
			vm.restrictionCount = resultData.restriction.count;
			vm.fieldPrefix = resultData.restriction.prefix;
			vm.fields = resultData.restriction.field;
		}

	}

	formatDate(inputDate : Date) {
		return this.zeroFill(inputDate.getDate(),2)  + "-" + this.zeroFill((inputDate.getMonth()+1),2) + "-" + inputDate.getFullYear();
	}

	pickCompareCodeSet(field) {
		let querySelection: QuerySelection;
		let vm = this;
		QueryPickerDialog.open(this.$modal, querySelection)
			.result.then(function (resultData: QuerySelection) {

			vm.libraryService.getLibraryItem<EnterpriseLibraryItem>(resultData.id)
				.subscribe(
					(libraryItem) => {
						vm.libraryItem = libraryItem;

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

						for (var i = 0; i < vm.restrictions.length; ++i) {
							if (vm.restrictions[i].field == field) {
								vm.restrictions[i].filter = filter;
								break;
							}
						}
					}
				);
		});
	}

	pickCompareCode(field) {
		let vm = this;
		vm.codeCompareSelection = [];

		for (var i = 0; i < vm.restrictions.length; ++i) {
			if (vm.restrictions[i].field == field) {
				if (vm.restrictions[i].filter.codeSet!=null) {
					vm.codeCompareSelection = vm.restrictions[i].filter.codeSet.codeSetValue;
					break;
				}
			}
		}

		TermPickerDialog.open(this.$modal, vm.codeCompareSelection)
			.result.then(function(result : CodeSetValue[]) {

			var codeSet : CodeSet = {
				codingSystem : "ENDEAVOUR",
				codeSetValue : result
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

			for (var i = 0; i < vm.restrictions.length; ++i) {
				if (vm.restrictions[i].field == field) {
					vm.restrictions[i].filter = filter;
					break;
				}
			}

		});
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

		TermPickerDialog.open(this.$modal, vm.codeSelection)
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
						vm.dateEditor = false;
						vm.resultData.filter.splice(i, 1);
						vm.datetype = "";
					}
					break;
				case "value":
					if (f.field=="VALUE") {
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

	filterValueFromChange(value : any, valueField : any, testField : any) {
		var vm = this;

		if (!value)
			value="";

		var valueFrom : ValueFrom = {
			constant: value,
			absoluteUnit: "NUMERIC",
			relativeUnit: null,
			operator: "GREATER_THAN_OR_EQUAL_TO",
			testField: testField
		}

		var filter: Filter = {
			field: valueField,
			valueFrom: valueFrom,
			valueTo: null,
			codeSet: null,
			valueSet: null,
			codeSetLibraryItemUuid: null,
			negate: false
		};

		for (var i = 0; i < vm.resultData.filter.length; ++i) {
			var flt = vm.resultData.filter[i];

			if (flt.field==valueField && flt.valueFrom) {
				vm.resultData.filter.splice(i, 1);
			}
		}

		if (value!="")
			vm.resultData.filter.push(filter);

	}

	filterValueToChange(value : any, valueField : any, testField : any) {
		var vm = this;

		if (!value)
			value="";

		var valueTo : ValueTo = {
			constant: value,
			absoluteUnit: "NUMERIC",
			relativeUnit: null,
			operator: "LESS_THAN_OR_EQUAL_TO",
			testField: testField
		}

		var filter: Filter = {
			field: valueField,
			valueFrom: null,
			valueTo: valueTo,
			codeSet: null,
			valueSet: null,
			codeSetLibraryItemUuid: null,
			negate: false
		};

		for (var i = 0; i < vm.resultData.filter.length; ++i) {
			var flt = vm.resultData.filter[i];

			if (flt.field==valueField && flt.valueTo) {
				vm.resultData.filter.splice(i, 1);
			}
		}

		if (value!="")
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
			absoluteUnit: "DATE",
			relativeUnit: null,
			operator: "GREATER_THAN_OR_EQUAL_TO",
			testField: ""
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

	filterRelativeDateFromChange(value : any, period : any, dateField : any, testField : any) {
		var vm = this;

		if (!value)
			value="";

		var valueFrom : ValueFrom = {
			constant: value,
			absoluteUnit: null,
			relativeUnit: period,
			operator: "GREATER_THAN_OR_EQUAL_TO",
			testField: testField
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
			absoluteUnit: "DATE",
			relativeUnit: null,
			operator: "LESS_THAN_OR_EQUAL_TO",
			testField: ""
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

	filterRelativeDateToChange(value : any, period : any, dateField : any, testField : any) {
		var vm = this;

		if (!value)
			value="";

		var valueTo : ValueTo = {
			constant: value,
			absoluteUnit: null,
			relativeUnit: period,
			operator: "LESS_THAN_OR_EQUAL_TO",
			testField: testField
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

	filterCompareCodeChange(valueField : any, codeSet : any) {
		var vm = this;

		if (codeSet==null)
			return;

		var filter: Filter = {
			field: valueField,
			valueFrom: null,
			valueTo: null,
			codeSet: codeSet,
			valueSet: null,
			codeSetLibraryItemUuid: null,
			negate: false
		};

		for (var i = 0; i < vm.resultData.filter.length; ++i) {
			var flt = vm.resultData.filter[i];

			if (flt.field==valueField) {
				vm.resultData.filter.splice(i, 1);
			}
		}

		vm.resultData.filter.push(filter);
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

		if (vm.type=="3") {
			for (var i = vm.restrictions.length-1; i >= 0; --i) {
				var f = vm.restrictions[i].filter;
				if (vm.restrictions[i].field.indexOf("DATE") >= 0) {
					vm.filterRelativeDateFromChange(f.valueFrom.constant, f.valueFrom.relativeUnit, vm.restrictions[i].field, f.valueFrom.testField)
					vm.filterRelativeDateToChange(f.valueTo.constant, f.valueTo.relativeUnit, vm.restrictions[i].field, f.valueTo.testField)
				} else if (vm.restrictions[i].field.indexOf("VALUE") >= 0) {
					vm.filterValueFromChange(f.valueFrom.constant, vm.restrictions[i].field, f.valueFrom.testField)
					vm.filterValueToChange(f.valueTo.constant, vm.restrictions[i].field, f.valueTo.testField)
				} else if (vm.restrictions[i].field.indexOf("CODE") >= 0) {
					vm.filterCompareCodeChange(vm.restrictions[i].field, f.codeSet);
				}
			}
		}

		if (vm.datetype == 'absolute') {
			vm.filterDateFromChange(vm.filterDateFrom);
			vm.filterDateToChange(vm.filterDateTo);
		} else if (vm.datetype == 'relative') {
			vm.filterRelativeDateFromChange(vm.filterDateFromRelativeValue, vm.filterDateFromRelativePeriod,"EFFECTIVE_DATE", "")
			vm.filterRelativeDateToChange(vm.filterDateToRelativeValue, vm.filterDateToRelativePeriod, "EFFECTIVE_DATE", "")
		}

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

		console.log(vm.resultData);


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

	setSelectedFields(option, event) {
		var vm = this;

		for (var i = 0; i < vm.keepFields.length; i++) {
			if (event.target.checked&&vm.keepFields[i]==option){
				vm.fields.push(vm.keepFields[i]);
			} else if (!event.target.checked&&vm.keepFields[i]==option) {
				let j = vm.fields.indexOf(option);
				vm.fields.splice(j,1);
			}
		}
		this.restrictionChange('1');
	}

	restrictionChange(value : any) {
		var vm = this;

		if (value == '' || value == 'ALL')
			vm.removeFilter('restriction');

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

	getCodeTermList(restriction) {
		return restriction.filter.codeSet.codeSetValue
			.map(val => val.term)
			.join(', ')
	}

	ok() {
		this.activeModal.close(this.resultData);
	}

	cancel() {
		this.activeModal.dismiss('cancel');
	}
}
