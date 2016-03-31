/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import IModalService = angular.ui.bootstrap.IModalService;
	import CodePickerController = app.dialogs.CodePickerController;
	import ICodingService = app.core.ICodingService;
	import Code = app.models.Code;
	import Test = app.models.Test;
	import DataSource = app.models.DataSource;
	import FieldTest = app.models.FieldTest;
	import CodeSet = app.models.CodeSet;
	import CodeSetValue = app.models.CodeSetValue;
	import ValueFrom = app.models.ValueFrom;
	import ValueTo = app.models.ValueTo;
	import Value = app.models.Value;
	import ValueAbsoluteUnit = app.models.ValueAbsoluteUnit;
	import ValueFromOperator = app.models.ValueFromOperator;
	import ValueToOperator = app.models.ValueToOperator;
	import IsAny = app.models.IsAny;
	import Concept = app.models.Concept;
	import Restriction = app.models.Restriction;

	'use strict';

	export class TestEditorController extends BaseDialogController {
		title : string;
		dataSourceOnly : boolean = false;
		viewFieldTest : boolean = false;
		codeEditor : boolean = false;
		dateEditor : boolean = false;
		dobEditor : boolean = false;
		valueEditor : boolean = false;
		sexEditor : boolean = false;
		valueField : string;
		dateLabel : string;
		dobLabel : string;
		sexLabel : string;
		fieldTestCodeEditor : boolean = false;
		fieldTestDateEditor : boolean = false;
		fieldTestDobEditor : boolean = false;
		fieldTestValueEditor : boolean = false;
		fieldTestSexEditor : boolean = false;
		fieldTestValueField : string;
		fieldTestDateLabel : string;
		fieldTestDobLabel : string;
		fieldTestSexLabel : string;
		addRestriction : boolean = false;
		showRestriction : boolean = false;
		addFilter : boolean = false;
		ruleDatasource : string;
		filterDateFrom : Date;
		filterDateTo : Date;
		filterDOBFrom : Date;
		filterDOBTo : Date;
		filterValueFrom : string;
		filterValueTo : string;
		filterSex : string;
		fieldTestDateFrom : Date;
		fieldTestDateTo : Date;
		fieldTestDOBFrom : Date;
		fieldTestDOBTo : Date;
		fieldTestValueFrom : string;
		fieldTestValueTo : string;
		fieldTestSex : string;
		restrictionFieldName: string;
		restrictionOrderDirection: string;
		restrictionCount: string;
		codeFilter : boolean = false;
		dateFilter : boolean = false;
		valueFilter : boolean = false;
		dobFilter : boolean = false;
		sexFilter : boolean = false;
		ageFilter : boolean = false;
		regFilter : boolean = false;

		editMode : boolean = false;

		codeSelection : CodeSetValue[] = [];
		fieldTestCodeSelection : CodeSetValue[] = [];
		termCache : any;

		datasources = ['','PATIENT','OBSERVATION','MEDICATION_ISSUE'];
		sortorders = ['','ASCENDING','DESCENDING'];
		fields = ['','EFFECTIVE_DATE','TIMESTAMP','VALUE'];
		genders = ['','MALE','FEMALE','UNKNOWN'];

		public static open($modal : IModalService, test : Test, dataSourceOnly : boolean) : IModalServiceInstance {
			var options : IModalSettings = {
				templateUrl:'app/dialogs/testEditor/testEditor.html',
				controller:'TestEditorController',
				controllerAs:'testEditor',
				size:'lg',
				backdrop: 'static',
				resolve:{
					test : () => test,
					dataSourceOnly : () => dataSourceOnly
				}
			};

			var dialog = $modal.open(options);
			return dialog;
		}

		static $inject = ['$uibModalInstance', 'LoggerService', '$uibModal', 'test', 'CodingService', 'dataSourceOnly'];

		constructor(protected $uibModalInstance : IModalServiceInstance,
					private logger : app.blocks.ILoggerService,
					private $modal : IModalService,
					private test: Test,
					private codingService : ICodingService,
					dataSourceOnly : boolean) {

			super($uibModalInstance);

			var vm = this;

			this.termCache = {};
			this.resultData = test;
			this.dataSourceOnly = dataSourceOnly;

			var ds : DataSource = {
				entity: "",
				dataSourceUuid: null,
				calculation: null,
				filter: [],
				restriction: null
			};

			var isAny : IsAny = {}

			var newTest : Test = {
				dataSource: ds,
				dataSourceUuid: null,
				isAny: isAny,
				fieldTest: []
			};

			if (!this.resultData)
				this.resultData = newTest;
			else
				this.initialiseEditMode(this.resultData);

			if (!this.dataSourceOnly) {
				vm.viewFieldTest = true;
				vm.title = "Test Editor"
			}
			else {
				vm.title = "Data Source Editor"
			}
		}

		initialiseEditMode(resultData : Test) {
			var vm = this;

			vm.ruleDatasource = resultData.dataSource.entity;
			this.dataSourceChange(resultData.dataSource.entity);

			vm.editMode = true;

			if (resultData.dataSource.filter === null) {
				resultData.dataSource.filter = [];
			}

			if (!vm.dataSourceOnly) {
				if (resultData.fieldTest === null) {
					resultData.fieldTest = [];
				}
			}

			for (var i = 0; i < resultData.dataSource.filter.length; ++i) {
				var filter = resultData.dataSource.filter[i];
				var field = filter.field;

				this.showFilter(field);

				switch(field) {
					case "CODE":
						vm.codeSelection = filter.codeSet[0].codeSetValue;
						break;
					case "DOB":
						if (filter.valueFrom)
							vm.filterDOBFrom = new Date(filter.valueFrom.constant);
						if (filter.valueTo)
							vm.filterDOBTo = new Date(filter.valueTo.constant);
						break;
					case "EFFECTIVE_DATE":
					case "REGISTRATION_DATE":
						if (filter.valueFrom)
							vm.filterDateFrom = new Date(filter.valueFrom.constant);
						if (filter.valueTo)
							vm.filterDateTo = new Date(filter.valueTo.constant);
						break;
					case "VALUE":
					case "AGE":
						if (filter.valueFrom)
							vm.filterValueFrom = filter.valueFrom.constant;
						if (filter.valueTo)
							vm.filterValueTo = filter.valueTo.constant;
						break;
					case "SEX":
						if (filter.valueEqualTo)
							vm.filterSex = filter.valueEqualTo.constant;
						break;
					default:
				}
			}

			if (!vm.dataSourceOnly) {
				for (var i = 0; i < resultData.fieldTest.length; ++i) {
					var fieldTest = resultData.fieldTest[i];
					var field = fieldTest.field;

					this.showFieldTest(field);

					switch(field) {
						case "CODE":
							vm.fieldTestCodeSelection = fieldTest.codeSet[0].codeSetValue;
							break;
						case "DOB":
							if (fieldTest.valueFrom)
								vm.fieldTestDOBFrom = new Date(fieldTest.valueFrom.constant);
							if (fieldTest.valueTo)
								vm.fieldTestDOBTo = new Date(fieldTest.valueTo.constant);
							break;
						case "EFFECTIVE_DATE":
						case "REGISTRATION_DATE":
							if (fieldTest.valueFrom)
								vm.fieldTestDateFrom = new Date(fieldTest.valueFrom.constant);
							if (fieldTest.valueTo)
								vm.fieldTestDateTo = new Date(fieldTest.valueTo.constant);
							break;
						case "VALUE":
						case "AGE":
							if (fieldTest.valueFrom)
								vm.fieldTestValueFrom = fieldTest.valueFrom.constant;
							if (fieldTest.valueTo)
								vm.fieldTestValueTo = fieldTest.valueTo.constant;
							break;
						case "SEX":
							if (fieldTest.valueEqualTo)
								vm.fieldTestSex = fieldTest.valueEqualTo.constant;
							break;
						default:
					}
				}

			}

			if (resultData.dataSource.restriction) {
				vm.showRestriction = true;
				vm.restrictionFieldName = resultData.dataSource.restriction.fieldName;
				vm.restrictionOrderDirection = resultData.dataSource.restriction.orderDirection;
				vm.restrictionCount = resultData.dataSource.restriction.count.toString();
			}

		}

		formatDate(inputDate : Date) {
			return this.zeroFill(inputDate.getDate(),2)  + "-" + this.zeroFill((inputDate.getMonth()+1),2) + "-" + inputDate.getFullYear();
		}

		showCodePicker() {
			var vm = this;

			CodePickerController.open(this.$modal, vm.codeSelection)
				.result.then(function(resultData : CodeSetValue[]){

				if (vm.codeSelection.length>0) {
					for (var i = 0; i < vm.resultData.dataSource.filter.length; ++i) {
						var filter = vm.resultData.dataSource.filter[i];

						if (filter.field=="CODE")
							vm.resultData.dataSource.filter.splice(i, 1);
					}
				}

				vm.codeSelection = resultData;

				var codeSet : CodeSet = {
					codingSystem : "SNOMED_CT",
					codeSetValue : resultData
				}

				var fieldTest : FieldTest = {
					field: "CODE",
					valueFrom: null,
					valueTo: null,
					valueRange: null,
					valueEqualTo: null,
					codeSet: [],
					codeSetLibraryItemUuid: null,
					negate: false
				};

				fieldTest.codeSet.push(codeSet);

				vm.resultData.dataSource.filter.push(fieldTest);
			});
		}

		showFieldTestCodePicker() {
			var vm = this;

			CodePickerController.open(this.$modal, vm.fieldTestCodeSelection)
				.result.then(function(resultData : CodeSetValue[]){

				if (vm.fieldTestCodeSelection.length>0) {
					for (var i = 0; i < vm.resultData.fieldTest.length; ++i) {
						var fTest = vm.resultData.fieldTest[i];

						if (fTest.field=="CODE")
							vm.resultData.fieldTest.splice(i, 1);
					}
				}

				vm.fieldTestCodeSelection = resultData;

				var codeSet : CodeSet = {
					codingSystem : "SNOMED_CT",
					codeSetValue : resultData
				}

				var fieldTest : FieldTest = {
					field: "CODE",
					valueFrom: null,
					valueTo: null,
					valueRange: null,
					valueEqualTo: null,
					codeSet: [],
					codeSetLibraryItemUuid: null,
					negate: false
				};

				fieldTest.codeSet.push(codeSet);

				vm.resultData.fieldTest.push(fieldTest);
			});
		}

		dataSourceChange(value : any) {
			var vm = this;

			this.resultData.dataSource.entity = value;

			vm.codeEditor = false;
			vm.dateEditor = false;
			vm.dobEditor = false;
			vm.valueEditor = false;
			vm.sexEditor = false;
			vm.fieldTestCodeEditor = false;
			vm.fieldTestDateEditor = false;
			vm.fieldTestDobEditor = false;
			vm.fieldTestValueEditor = false;
			vm.fieldTestSexEditor = false;
			vm.addRestriction = true;
			vm.showRestriction = false;
			vm.addFilter = true;
			vm.codeFilter = false;
			vm.dateFilter = false;
			vm.valueFilter = false;
			vm.dobFilter = false;
			vm.sexFilter = false;
			vm.ageFilter = false;
			vm.regFilter = false;
			vm.viewFieldTest = true;

			switch(value) {
				case "OBSERVATION":
					vm.codeFilter = true;
					vm.dateFilter = true;
					vm.valueFilter = true;
					break;
				case "MEDICATION_ISSUE":
					vm.codeFilter = true;
					vm.dateFilter = true;
					break;
				case "PATIENT":
					vm.dobFilter = true;
					vm.sexFilter = true;
					vm.ageFilter = true;
					vm.regFilter = true;
					break;
				default:
			}
		};

		showFilter(value : any) {
			var vm = this;

			switch(value) {
				case "CODE":
					vm.codeEditor = true;
					break;
				case "DOB":
					vm.dobEditor = true;
					vm.dobLabel = value;
					break;
				case "EFFECTIVE_DATE":
				case "REGISTRATION_DATE":
					vm.dateEditor = true;
					vm.dateLabel = value;
					break;
				case "VALUE":
				case "AGE":
					vm.valueEditor = true;
					vm.valueField = value;
					break;
				case "SEX":
					vm.sexEditor = true;
					vm.sexLabel = value;
					break;
				default:
			}
		}

		showFieldTest(value : any) {
			var vm = this;

			switch(value) {
				case "CODE":
					vm.fieldTestCodeEditor = true;
					break;
				case "DOB":
					vm.fieldTestDobEditor = true;
					vm.fieldTestDobLabel = value;
					break;
				case "EFFECTIVE_DATE":
				case "REGISTRATION_DATE":
					vm.fieldTestDateEditor = true;
					vm.fieldTestDateLabel = value;
					break;
				case "VALUE":
				case "AGE":
					vm.fieldTestValueEditor = true;
					vm.fieldTestValueField = value;
					break;
				case "SEX":
					vm.fieldTestSexEditor = true;
					vm.fieldTestSexLabel = value;
					break;
				default:
			}
		}

		filterDateFromChange(value : any, dateField : any) {
			var vm = this;

			if (!value)
				value="";

			var datestring : string = "";

			if (value!="" && value!=null)
				datestring = value.getFullYear()  + "-" + this.zeroFill((value.getMonth()+1),2) + "-" + this.zeroFill(value.getDate(),2);

			var valueFrom : ValueFrom = {
				constant: datestring,
				parameter: null,
				absoluteUnit: "DATE",
				relativeUnit: null,
				operator: "GREATER_THAN_OR_EQUAL_TO"
			}

			var fieldTest : FieldTest = {
				field: dateField,
				valueFrom: valueFrom,
				valueTo: null,
				valueRange: null,
				valueEqualTo: null,
				codeSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			var foundEntry : boolean = false;

			for (var i = 0; i < vm.resultData.dataSource.filter.length; ++i) {
				var filter = vm.resultData.dataSource.filter[i];

				if (filter.field==dateField && filter.valueFrom && value!="" && value!=null) {
					foundEntry = true;
					filter.valueFrom = valueFrom;
					break;
				}
				else if (filter.field==dateField && filter.valueFrom && (value=="" || value==null))
					vm.resultData.dataSource.filter.splice(i, 1);
			}

			if (!foundEntry && value!="" && value!=null)
				vm.resultData.dataSource.filter.push(fieldTest);
		}

		filterDateToChange(value : any, dateField : any) {
			var vm = this;

			if (!value)
				value="";

			var datestring : string = "";

			if (value!="" && value!=null)
				datestring = value.getFullYear()  + "-" + this.zeroFill((value.getMonth()+1),2) + "-" + this.zeroFill(value.getDate(),2);

			var valueTo : ValueTo = {
				constant: datestring,
				parameter: null,
				absoluteUnit: "DATE",
				relativeUnit: null,
				operator: "LESS_THAN_OR_EQUAL_TO"
			}

			var fieldTest : FieldTest = {
				field: dateField,
				valueFrom: null,
				valueTo: valueTo,
				valueRange: null,
				valueEqualTo: null,
				codeSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			var foundEntry : boolean = false;

			for (var i = 0; i < vm.resultData.dataSource.filter.length; ++i) {
				var filter = vm.resultData.dataSource.filter[i];

				if (filter.field==dateField && filter.valueTo && value!="" && value!=null) {
					foundEntry = true;
					filter.valueTo = valueTo;
					break;
				}
				else if (filter.field==dateField && filter.valueTo && (value=="" || value==null))
					vm.resultData.dataSource.filter.splice(i, 1);
			}

			if (!foundEntry && value!="" && value!=null)
				vm.resultData.dataSource.filter.push(fieldTest);
		}

		zeroFill( number : any, width : any ) {
			width -= number.toString().length;
			if ( width > 0 )
			{
				return new Array( width + (/\./.test( number ) ? 2 : 1) ).join( '0' ) + number;
			}
			return number + ""; // always return a string
		}

		filterValueChange(value : any, valueField : any) {
			var vm = this;

			if (!value)
				value="";

			var valueEqualTo : Value = {
				constant: value,
				parameter: null,
				absoluteUnit: "NUMERIC",
				relativeUnit: null
			}

			var fieldTest : FieldTest = {
				field: valueField,
				valueFrom: null,
				valueTo: null,
				valueRange: null,
				valueEqualTo: valueEqualTo,
				codeSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			var foundEntry : boolean = false;

			for (var i = 0; i < vm.resultData.dataSource.filter.length; ++i) {
				var filter = vm.resultData.dataSource.filter[i];

				if (filter.field==valueField && filter.valueEqualTo && value!="") {
					foundEntry = true;
					filter.valueEqualTo = valueEqualTo;
					break;
				}
				else if (filter.field==valueField && filter.valueEqualTo && value=="")
					vm.resultData.dataSource.filter.splice(i, 1);
			}

			if (!foundEntry && value!="")
				vm.resultData.dataSource.filter.push(fieldTest);
		}

		filterValueFromChange(value : any) {
			var vm = this;

			if (!value)
				value="";

			var valueFrom : ValueFrom = {
				constant: value,
				parameter: null,
				absoluteUnit: "NUMERIC",
				relativeUnit: null,
				operator: "GREATER_THAN_OR_EQUAL_TO"
			}

			var fieldTest : FieldTest = {
				field: vm.valueField,
				valueFrom: valueFrom,
				valueTo: null,
				valueRange: null,
				valueEqualTo: null,
				codeSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			var foundEntry : boolean = false;

			for (var i = 0; i < vm.resultData.dataSource.filter.length; ++i) {
				var filter = vm.resultData.dataSource.filter[i];

				if (filter.field==vm.valueField && filter.valueFrom && value!="") {
					foundEntry = true;
					filter.valueFrom = valueFrom;
					break;
				}
				else if (filter.field==vm.valueField && filter.valueFrom && value=="")
					vm.resultData.dataSource.filter.splice(i, 1);
			}

			if (!foundEntry && value!="")
				vm.resultData.dataSource.filter.push(fieldTest);
		}

		filterValueToChange(value : any) {
			var vm = this;

			if (!value)
				value="";

			var valueTo : ValueTo = {
				constant: value,
				parameter: null,
				absoluteUnit: "NUMERIC",
				relativeUnit: null,
				operator: "LESS_THAN_OR_EQUAL_TO"
			}

			var fieldTest : FieldTest = {
				field: vm.valueField,
				valueFrom: null,
				valueTo: valueTo,
				valueRange: null,
				valueEqualTo: null,
				codeSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			var foundEntry : boolean = false;

			for (var i = 0; i < vm.resultData.dataSource.filter.length; ++i) {
				var filter = vm.resultData.dataSource.filter[i];

				if (filter.field==vm.valueField && filter.valueTo && value!="") {
					foundEntry = true;
					filter.valueTo = valueTo;
					break;
				}
				else if (filter.field==vm.valueField && filter.valueTo && value=="")
					vm.resultData.dataSource.filter.splice(i, 1);
			}

			if (!foundEntry && value!="")
				vm.resultData.dataSource.filter.push(fieldTest);

		}

		fieldTestDateFromChange(value : any, dateField : any) {
			var vm = this;

			if (!value)
				value="";

			var datestring : string = "";

			if (value!="" && value!=null)
				datestring = value.getFullYear()  + "-" + this.zeroFill((value.getMonth()+1),2) + "-" + this.zeroFill(value.getDate(),2);

			var valueFrom : ValueFrom = {
				constant: datestring,
				parameter: null,
				absoluteUnit: "DATE",
				relativeUnit: null,
				operator: "GREATER_THAN_OR_EQUAL_TO"
			}

			var fieldTest : FieldTest = {
				field: dateField,
				valueFrom: valueFrom,
				valueTo: null,
				valueRange: null,
				valueEqualTo: null,
				codeSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			var foundEntry : boolean = false;

			for (var i = 0; i < vm.resultData.fieldTest.length; ++i) {
				var ftest = vm.resultData.fieldTest[i];

				if (ftest.field==dateField && ftest.valueFrom && value!="" && value!=null) {
					foundEntry = true;
					fieldTest.valueFrom = valueFrom;
					break;
				}
				else if (ftest.field==dateField && ftest.valueFrom && (value=="" || value==null))
					vm.resultData.fieldTest.splice(i, 1);
			}

			if (!foundEntry && value!="" && value!=null)
				vm.resultData.fieldTest.push(fieldTest);
		}

		fieldTestDateToChange(value : any, dateField : any) {
			var vm = this;

			if (!value)
				value="";

			var datestring : string = "";

			if (value!="" && value!=null)
				datestring = value.getFullYear()  + "-" + this.zeroFill((value.getMonth()+1),2) + "-" + this.zeroFill(value.getDate(),2);

			var valueTo : ValueTo = {
				constant: datestring,
				parameter: null,
				absoluteUnit: "DATE",
				relativeUnit: null,
				operator: "LESS_THAN_OR_EQUAL_TO"
			}

			var fieldTest : FieldTest = {
				field: dateField,
				valueFrom: null,
				valueTo: valueTo,
				valueRange: null,
				valueEqualTo: null,
				codeSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			var foundEntry : boolean = false;

			for (var i = 0; i < vm.resultData.fieldTest.length; ++i) {
				var ftest = vm.resultData.fieldTest[i];

				if (ftest.field==dateField && ftest.valueTo && value!="" && value!=null) {
					foundEntry = true;
					fieldTest.valueTo = valueTo;
					break;
				}
				else if (ftest.field==dateField && ftest.valueTo && (value=="" || value==null))
					vm.resultData.fieldTest.splice(i, 1);
			}

			if (!foundEntry && value!="" && value!=null)
				vm.resultData.fieldTest.push(fieldTest);
		}

		fieldTestValueChange(value : any, valueField : any) {
			var vm = this;

			if (!value)
				value="";

			var valueEqualTo : Value = {
				constant: value,
				parameter: null,
				absoluteUnit: "NUMERIC",
				relativeUnit: null
			}

			var fieldTest : FieldTest = {
				field: valueField,
				valueFrom: null,
				valueTo: null,
				valueRange: null,
				valueEqualTo: valueEqualTo,
				codeSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			var foundEntry : boolean = false;

			for (var i = 0; i < vm.resultData.fieldTest.length; ++i) {
				var ftest = vm.resultData.fieldTest[i];

				if (ftest.field==valueField && ftest.valueEqualTo && value!="") {
					foundEntry = true;
					fieldTest.valueEqualTo = valueEqualTo;
					break;
				}
				else if (ftest.field==valueField && ftest.valueEqualTo && value=="")
					vm.resultData.fieldTest.splice(i, 1);
			}

			if (!foundEntry && value!="")
				vm.resultData.fieldTest.push(fieldTest);
		}

		fieldTestValueFromChange(value : any) {
			var vm = this;

			if (!value)
				value="";

			var valueFrom : ValueFrom = {
				constant: value,
				parameter: null,
				absoluteUnit: "NUMERIC",
				relativeUnit: null,
				operator: "GREATER_THAN_OR_EQUAL_TO"
			}

			var fieldTest : FieldTest = {
				field: vm.fieldTestValueField,
				valueFrom: valueFrom,
				valueTo: null,
				valueRange: null,
				valueEqualTo: null,
				codeSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			var foundEntry : boolean = false;

			for (var i = 0; i < vm.resultData.fieldTest.length; ++i) {
				var ftest = vm.resultData.fieldTest[i];

				if (ftest.field==vm.fieldTestValueField && ftest.valueFrom && value!="") {
					foundEntry = true;
					fieldTest.valueFrom = valueFrom;
					break;
				}
				else if (ftest.field==vm.fieldTestValueField && ftest.valueFrom && value=="")
					vm.resultData.fieldTest.splice(i, 1);
			}

			if (!foundEntry && value!="")
				vm.resultData.fieldTest.push(fieldTest);
		}

		fieldTestValueToChange(value : any) {
			var vm = this;

			if (!value)
				value="";

			var valueTo : ValueTo = {
				constant: value,
				parameter: null,
				absoluteUnit: "NUMERIC",
				relativeUnit: null,
				operator: "LESS_THAN_OR_EQUAL_TO"
			}

			var fieldTest : FieldTest = {
				field: vm.fieldTestValueField,
				valueFrom: null,
				valueTo: valueTo,
				valueRange: null,
				valueEqualTo: null,
				codeSet: null,
				codeSetLibraryItemUuid: null,
				negate: false
			};

			var foundEntry : boolean = false;

			for (var i = 0; i < vm.resultData.fieldTest.length; ++i) {
				var ftest = vm.resultData.fieldTest[i];

				if (ftest.field==vm.fieldTestValueField && ftest.valueTo && value!="") {
					foundEntry = true;
					fieldTest.valueTo = valueTo;
					break;
				}
				else if (ftest.field==vm.fieldTestValueField && ftest.valueTo && value=="")
					vm.resultData.fieldTest.splice(i, 1);
			}

			if (!foundEntry && value!="")
				vm.resultData.fieldTest.push(fieldTest);

		}

		restrictionChange(value : any) {
			var vm = this;

			if (!value || vm.restrictionFieldName=="" || vm.restrictionOrderDirection=="" || vm.restrictionCount=="") {
				vm.resultData.dataSource.restriction = null;
				return;
			}

			var restriction : Restriction = {
				fieldName: vm.restrictionFieldName,
				orderDirection: vm.restrictionOrderDirection,
				count: Number(vm.restrictionCount)
			};

			vm.resultData.dataSource.restriction = restriction;
		}

		toggleRestriction() {
			var vm = this;

			vm.showRestriction = !vm.showRestriction;
		};

		save() {
			var vm = this;

			if (!vm.dataSourceOnly) {
				for (var i = 0; i < vm.resultData.fieldTest.length; ++i) {
					var ft = vm.resultData.fieldTest[i];
					if (ft.field=="CODE") {
						if (ft.codeSet[0].codeSetValue.length==0) {
							vm.resultData.fieldTest.splice(i, 1);
						}
					}
				}

				if (vm.resultData.fieldTest.length>0)
					vm.resultData.isAny = null;
				else
					vm.resultData.isAny = {};
			}

			this.ok();
		}

		termShorten(term : string) {
			term = term.replace(' (disorder)','');
			term = term.replace(' (observable entity)','');
			term = term.replace(' (finding)','');
			return term;
		}

		getTerm(code : string) : string {
			var vm = this;
			var term = vm.termCache[code];
			if (term) { return term; }
			vm.termCache[code] = 'Loading...';

			vm.codingService.getPreferredTerm(code)
				.then(function(concept : Concept) {
					vm.termCache[code] = vm.termShorten(concept.preferredTerm);
				});

			return vm.termCache[code];
		}
	}

	angular
		.module('app.dialogs')
		.controller('TestEditorController', TestEditorController);
}
