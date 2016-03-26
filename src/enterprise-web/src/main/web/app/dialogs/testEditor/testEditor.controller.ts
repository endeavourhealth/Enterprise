/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import IModalService = angular.ui.bootstrap.IModalService;
	import CodePickerController = app.dialogs.CodePickerController;
	import TermlexCode = app.models.Code;
	import TermlexCodeSelection = app.models.CodeSelection;
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

	'use strict';

	export class TestEditorController extends BaseDialogController {
		calculationEditor : boolean = false;
		codeEditor : boolean = false;
		dateEditor : boolean = false;
		dobEditor : boolean = false;
		valueEditor : boolean = false;
		sexEditor : boolean = false;
		valueField : string;
		dateLabel : string;
		dobLabel : string;
		sexLabel : string;
		addRestriction : boolean = false;
		showRestriction : boolean = false;
		addFilter : boolean = false;
		ruleDatasource : string;
		ruleFilter : string;
		filterCodes : string;
		filterDateFrom : Date;
		filterDateTo : Date;
		filterDOBFrom : Date;
		filterDOBTo : Date;
		filterValueFrom : string;
		filterValueTo : string;
		filterSex : string;
		codeFilter : boolean = false;
		dateFilter : boolean = false;
		valueFilter : boolean = false;
		dobFilter : boolean = false;
		sexFilter : boolean = false;
		ageFilter : boolean = false;
		regFilter : boolean = false;

		editMode : boolean = false;

		codeSelection : TermlexCodeSelection[] = [];

		datasources = ['','PATIENT','OBSERVATION','MEDICATION_ISSUE','CALCULATION'];
		sortorders = ['','LATEST','EARLIEST','HIGHEST','LOWEST'];
		periods = ['','DAYS','WEEKS','MONTHS','YEARS'];
		rule = [''];
		fields = ['','EFFECTIVE_DATE','TIMESTAMP','VALUE'];
		functions = ['','AVERAGE','COUNT','MIN','MAX'];
		genders = ['','MALE','FEMALE','UNKNOWN'];

		public static open($modal : IModalService, test : Test) : IModalServiceInstance {
			var options : IModalSettings = {
				templateUrl:'app/dialogs/testEditor/testEditor.html',
				controller:'TestEditorController',
				controllerAs:'testEditor',
				size:'lg',
				backdrop: 'static',
				resolve:{
					test : () => test
				}
			};

			var dialog = $modal.open(options);
			return dialog;
		}

		static $inject = ['$uibModalInstance', 'LoggerService', '$uibModal', 'test'];

		constructor(protected $uibModalInstance : IModalServiceInstance,
					private logger:app.blocks.ILoggerService,
					private $modal : IModalService,
					private test: Test) {

			super($uibModalInstance);

			this.resultData = test;

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
				 fieldTest: null
			 };

			if (!this.resultData)
				this.resultData = newTest;
			else
				this.initialiseEditMode(this.resultData);
		}

		initialiseEditMode(resultData : Test) {
			var vm = this;

			vm.ruleDatasource = resultData.dataSource.entity;
			this.dataSourceChange(resultData.dataSource.entity);

			vm.editMode = true;

			for (var i = 0; i < resultData.dataSource.filter.length; ++i) {
				var filter = resultData.dataSource.filter[i];
				var field = filter.field;
				this.showFilter(field);
				vm.ruleFilter = field;

				switch(field) {
					case "CODE":
						var terms = "";

						for (var c = 0; c < filter.codeSet[0].codeSetValue.length; ++c) {
							var codes = filter.codeSet[0].codeSetValue[c];

							var term = codes.term;
							terms+=", "+term;

							var excludedCodes : Code[] = [];

							for (var e = 0; e < codes.exclusion.length; ++e) {
								var exclusion = codes.exclusion[e];

								var term = exclusion.term;
								terms+=", "+term+" (exclusion)";

								var excl : Code = {
									id: exclusion.code,
									label: exclusion.term
								}

								excludedCodes.push(excl)
							}

							var selectedCodes : TermlexCodeSelection = {
								id: codes.code,
								label: codes.term,
								includeChildren: codes.includeChildren,
								exclusions: excludedCodes
							}

							this.codeSelection.push(selectedCodes);
						}

						vm.filterCodes = terms.substring(2);

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
		}

		formatDate(inputDate : Date) {
			return this.zeroFill(inputDate.getDate(),2)  + "-" + this.zeroFill((inputDate.getMonth()+1),2) + "-" + inputDate.getFullYear();
		}

		showCodePicker() {
			var vm = this;

			CodePickerController.open(this.$modal, this.codeSelection)
				.result.then(function(resultData : TermlexCodeSelection[]){

				var codeSet : CodeSet = {
					codingSystem : "SNOMED_CT",
					codeSetValue : []
				}

				var terms = "";

				for (var i = 0; i < resultData.length; ++i) {
					var code = resultData[i];

					var codeSetVal : CodeSetValue = {
						code : "",
						term : "",
						includeChildren : true,
						exclusion : []
					}

					codeSetVal.code = code.id;
					codeSetVal.term = code.label;
					codeSetVal.includeChildren = code.includeChildren;

					var term = code.label;
					terms+=", "+term;

					for (var e = 0; e < code.exclusions.length; ++e) {
						var exclusion = code.exclusions[e];

						var codeSetValExcl : CodeSetValue = {
							code : "",
							term : "",
							includeChildren : true,
							exclusion : null
						}

						codeSetValExcl.code = exclusion.id;
						codeSetValExcl.term = exclusion.label;
						codeSetVal.exclusion.push(codeSetValExcl);

						var term = exclusion.label;
						terms+=", "+term+" (exclusion)";

					}

					codeSet.codeSetValue.push(codeSetVal);

				}

				vm.filterCodes = terms.substring(2);

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

				if (vm.codeSelection.length>0) {
					for (var i = 0; i < vm.resultData.dataSource.filter.length; ++i) {
						var filter = vm.resultData.dataSource.filter[i];

						if (filter.field=="CODE")
							vm.resultData.dataSource.filter.splice(i, 1);
					}
				}

				vm.resultData.dataSource.filter.push(fieldTest);
			});
		}

		dataSourceChange(value : any) {
			var vm = this;

			this.resultData.dataSource.entity = value;

			vm.codeFilter = false;
			vm.dateFilter = false;
			vm.valueFilter = false;
			vm.dobFilter = false;
			vm.sexFilter = false;
			vm.ageFilter = false;
			vm.regFilter = false;

			switch(value) {
				case "CALCULATION":
					this.showCalculationEditorFields();
					break;
				case "OBSERVATION":
					vm.codeFilter = true;
					vm.dateFilter = true;
					vm.valueFilter = true;
					this.showFilters();
					break;
				case "MEDICATION_ISSUE":
					vm.codeFilter = true;
					vm.dateFilter = true;
					this.showFilters();
					break;
				case "PATIENT":
					vm.dobFilter = true;
					vm.sexFilter = true;
					vm.ageFilter = true;
					vm.regFilter = true;
					this.showFilters();
					break;
				default:
					this.showFilters();
			}
		};

		showFilter(value : any) {
			var vm = this;

			vm.editMode = true;

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

		filterDateFromChange(value : any, dateField : any) {
			var vm = this;

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

		showCalculationEditorFields() {
			var vm = this;

			vm.calculationEditor = true;
			vm.codeEditor = false;
			vm.dateEditor = false;
			vm.dobEditor = false;
			vm.valueEditor = false;
			vm.sexEditor = false;
			vm.addRestriction = false;
			vm.showRestriction = false;
			vm.addFilter = false;
		}

		showFilters() {
			var vm = this;

			vm.calculationEditor = false;
			vm.codeEditor = false;
			vm.dateEditor = false;
			vm.dobEditor = false;
			vm.valueEditor = false;
			vm.sexEditor = false;
			vm.addRestriction = true;
			vm.addFilter = true;
		}

		toggleRestriction() {
			var vm = this;

			vm.showRestriction = !vm.showRestriction;
		};

		save() {
			this.ok();
		}

	}

	angular
		.module('app.dialogs')
		.controller('TestEditorController', TestEditorController);
}