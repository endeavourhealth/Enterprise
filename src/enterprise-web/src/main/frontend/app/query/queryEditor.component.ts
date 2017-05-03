import {Component} from "@angular/core";
import {Transition, StateService} from "ui-router-ng2";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

import {ExpressionEditDialog} from "../expressions/expressionEditor.dialog";
import {TestEditDialog} from "../tests/testEditor.dialog";
import {QueryPickerDialog} from "./queryPicker.dialog";
import {StartingRules} from "./models/StartingRules";
import {Query} from "./models/Query";
import {EnterpriseLibraryItem} from "../enterpriseLibrary/models/EnterpriseLibraryItem";
import {ExpressionType} from "../expressions/models/ExpressionType";
import {QuerySelection} from "./models/QuerySelection";
import {Test} from "../tests/models/Test";
import {LibraryService, LoggerService, MessageBoxDialog} from "eds-common-js";
import {flowchart} from "../flowchart/flowchart.viewmodel";

@Component({
	template : require('./queryEditor.html'),
	entryComponents : [
		QueryPickerDialog
	]
})
export class QueryEditComponent {
	private queryName: string;
	private queryDescription: string;
	private disableRuleProps: boolean;
	private zoomPercent: string;
	private zoomNumber: number;
	private ruleId: number;
	private nextRuleID: number;
	private chartViewModel: any;
	private results: any;
	private startingRules: StartingRules;
	private query: Query;
	private libraryItem: EnterpriseLibraryItem;
	private rulePassAction: string;
	private ruleFailAction: string;
	private ruleDescription: string;

	constructor(private logger: LoggerService,
							private transition: Transition,
							private $modal: NgbModal,
							private state: StateService,
							private libraryService: LibraryService) {
		this.queryName = "";
		this.queryDescription = "";
		this.disableRuleProps = false;
		this.zoomPercent = "100%";
		this.zoomNumber = 100;
		this.nextRuleID = 1;
		this.results = [
			{value: 'GOTO_RULES', displayName: 'Go to rule'},
			{value: 'INCLUDE', displayName: 'Include patient in final result'},
			{value: 'NO_ACTION', displayName: 'No further action'}
		];

		this.startingRules = {
			ruleId: []
		};

		this.query = {
			parentQueryUuid: null,
			startingRules: this.startingRules,
			rule: []
		};

		this.libraryItem = {
			uuid: null,
			name: null,
			description: null,
			folderUuid: transition.params()['itemUuid'],
			query: this.query,
		} as EnterpriseLibraryItem;

		this.createModel(this.libraryItem);

		this.performAction(transition.params()['itemAction'], transition.params()['itemUuid']);
	}

	private createModel(libraryItem: EnterpriseLibraryItem) {
		this.chartViewModel = new flowchart.ChartViewModel(libraryItem);
	}

	private performAction(itemAction: string, itemUuid: string) {
		switch (itemAction) {
			case "view":
			case "edit":
				this.load(itemUuid);
				break;
			default:
		}
	}

	load(itemUuid : string) {
		let vm = this;
		vm.libraryService.getLibraryItem<EnterpriseLibraryItem>(itemUuid)
			.subscribe(
				(libraryItem) => vm.processLibraryItem(libraryItem),
				(error) => vm.logger.error('Error loading cohort', error, 'Error')
			);
	}

	processLibraryItem(libraryItem : EnterpriseLibraryItem) {
		let vm = this;
		vm.createModel(libraryItem);

		vm.queryName = libraryItem.name;
		vm.queryDescription = libraryItem.description;


		let newStartRuleDataModel = {
			description: "START",
			id: 0,
			layout: {
				x: -162,
				y: 25
			},
			onPass: {
				action: "",
				ruleId: <any>[]
			},
			onFail: {
				action: "",
				ruleId: <any>[]
			}
		};

		vm.chartViewModel.addRule(newStartRuleDataModel);

		let highestId = 1;
		for (let i = 0; i < vm.chartViewModel.data.query.rule.length; ++i) {
			let id = vm.chartViewModel.data.query.rule[i].id;
			if (parseInt(id) > highestId) {
				highestId = parseInt(id);
			}
		}
		vm.nextRuleID = highestId + 1;
	}

	queryNameChange() {
		this.chartViewModel.data.name = this.queryName;
	};

	queryDescriptionChange() {
		this.chartViewModel.data.description = this.queryDescription;
	};

	ruleDescriptionChange() {
		let selectedRule = this.chartViewModel.getSelectedRule();
		selectedRule.data.description = this.ruleDescription;
	};

	rulePassActionChange() {
		let selectedRule = this.chartViewModel.getSelectedRule();
		selectedRule.data.onPass.action = this.rulePassAction;
		if (this.rulePassAction !== "GOTO_RULES") {
			selectedRule.data.onPass.ruleId = <any>[];
		}
	};

	ruleFailActionChange() {
		let selectedRule = this.chartViewModel.getSelectedRule();
		selectedRule.data.onFail.action = this.ruleFailAction;
		if (this.ruleFailAction !== "GOTO_RULES") {
			selectedRule.data.onFail.ruleId = <any>[];
		}
	};

	clearQuery() {
		let vm = this;
		MessageBoxDialog.open(vm.$modal, 'Clear Rules', 'Are you sure you want to clear the rules in this cohort (changes will not be saved)?', 'Yes', 'No')
			.result.then(function () {
			vm.chartViewModel.clearQuery();
			vm.ruleDescription = "";
			vm.rulePassAction = "";
			vm.ruleFailAction = "";
			vm.nextRuleID = 1;
		});
	}

	cancelChanges() {
		let vm = this;
		MessageBoxDialog.open(vm.$modal, 'Cancel Changes', 'Are you sure you want to cancel the editing of this cohort (changes will not be saved) ?', 'Yes', 'No')
			.result.then(function () {
			vm.logger.error('Cohort not saved');
			vm.state.go(vm.transition.from());
		});
	}

	zoomIn() {
		this.zoomNumber = this.zoomNumber + 10;
		if (this.zoomNumber > 100)
			this.zoomNumber = 100;
		this.zoomPercent = this.zoomNumber.toString() + "%";
	};

	zoomOut() {
		this.zoomNumber = this.zoomNumber - 10;
		if (this.zoomNumber < 50)
			this.zoomNumber = 50;
		this.zoomPercent = this.zoomNumber.toString() + "%";
	};

	//
	// Add a new rule to the chart.
	//
	addNewRule(type: any) {
		//
		// Template for a new rule.
		//

		if (this.nextRuleID === 1) { // Add to new Cohort

			if (type == 1 || type == 3) { // Feature or Test

				this.createStartRule(-162, 25);

				this.createNewRule(194, 5, type);
			}
			else if (type == 2) { // Cohort as a Feature
				let querySelection: QuerySelection;
				let vm = this;
				QueryPickerDialog.open(this.$modal, querySelection)
					.result.then(function (resultData: QuerySelection) {
					vm.createStartRule(-162, 25);
					vm.createNewQueryRule(194, 5, resultData);
				});
			}

			this.chartViewModel.addStartingRule(1);
		}
		else { // Add to existing Cohort

			switch (type) {
				case "1": // Feature
				case "3": // Test
					this.createNewRule(566, 7, type);
					break;
				case "2": // Cohort as a Feature
					let querySelection: QuerySelection;
					let vm = this;
					QueryPickerDialog.open(this.$modal, querySelection)
						.result.then(function (resultData: QuerySelection) {
						vm.createNewQueryRule(566, 7, resultData);
					});
					break;
				case "4": // Expression/Function
					this.createNewExpression(566, 7);
					break;
			}
		}
	};

	createStartRule(x: any, y: any) {
		let newStartRuleDataModel = {
			description: "START",
			id: 0,
			layout: {
				x: x,
				y: y
			},
			onPass: {
				action: "",
				ruleId: <any>[]
			},
			onFail: {
				action: "",
				ruleId: <any>[]
			}
		};

		this.chartViewModel.addRule(newStartRuleDataModel);
	}

	createNewRule(x: any, y: any, type: any) {

		var label = "";
		if (type=="1") {
			label = "Feature Description"
		} else
		if (type=="3") {
			label = "Test Description"
		}

		let newRuleDataModel = {
			description: label,
			id: this.nextRuleID++,
			type: type,
			layout: {
				x: x,
				y: y
			},
			onPass: {
				action: "INCLUDE",
				ruleId: <any>[]
			},
			onFail: {
				action: "NO_ACTION",
				ruleId: <any>[]
			}
		};
		this.chartViewModel.addRule(newRuleDataModel);
	}

	createNewExpression(x: any, y: any) {
		let newExpressionRuleDataModel = {
			description: "Function Description",
			id: this.nextRuleID++,
			type: '4',
			layout: {
				x: x,
				y: y
			},
			onPass: {
				action: "INCLUDE",
				ruleId: <any>[]
			},
			onFail: {
				action: "NO_ACTION",
				ruleId: <any>[]
			},
			expression: {
				expressionText: "",
				variable: <any>[]
			}
		};
		this.chartViewModel.addRule(newExpressionRuleDataModel);
	}

	createNewQueryRule(x: any, y: any, resultData: any) {
		let newQueryRuleDataModel = {
			description: resultData.name + "~" + resultData.description,
			id: this.nextRuleID++,
			type: "2",
			layout: {
				x: x,
				y: y
			},
			onPass: {
				action: "INCLUDE",
				ruleId: <any>[]
			},
			onFail: {
				action: "NO_ACTION",
				ruleId: <any>[]
			},
			queryLibraryItemUUID: resultData.id
		};

		this.chartViewModel.addRule(newQueryRuleDataModel);
	}

	//
	// Delete selected rule and connections.
	//
	deleteSelected() {
		this.chartViewModel.deleteSelected();
	};

	save(close: boolean) {
		let vm = this;
		if (vm.queryName === "") {
			vm.logger.error('Please enter a name for the cohort');
			return;
		}

		if (vm.chartViewModel.data.query.rule.length === 0) {
			vm.logger.error('Please create a rule in this cohort');
			return;
		}

		for (let i = 0; i < vm.chartViewModel.data.query.rule.length; ++i) {
			let rule = vm.chartViewModel.data.query.rule[i];
			if (!rule.test && !rule.expression && !rule.queryLibraryItemUUID && rule.description !== "START") {
				vm.logger.error('Rule "' + rule.description + '" does not have any criteria');
				return;
			}
		}

		for (let i = 0; i < vm.chartViewModel.data.query.rule.length; ++i) {
			let rule = vm.chartViewModel.data.query.rule[i];
			if (!rule.test && (rule.expression && rule.expression.variable.length === 0) && rule.description !== "START") {
				vm.logger.error('Function "' + rule.description + '" does not have any variables');
				return;
			}
		}

		for (let i = 0; i < vm.chartViewModel.data.query.rule.length; ++i) {
			let rule = vm.chartViewModel.data.query.rule[i];
			if (rule.description !== "START") {
				if (rule.onPass.action === "") {
					vm.logger.error('Rule "' + rule.description + '" does not have a PASS action');
					return;
				}
				if (rule.onFail.action === "") {
					vm.logger.error('Rule "' + rule.description + '" does not have a FAIL action');
					return;
				}
			}
		}

		for (let i = 0; i < vm.chartViewModel.data.query.rule.length; ++i) {
			if (vm.chartViewModel.data.query.rule[i].description === "START") {
				vm.chartViewModel.data.query.rule.splice(i, 1);
				vm.chartViewModel.rule.splice(i, 1);
			}

		}

		let libraryItem = vm.chartViewModel.data;

		vm.libraryService.saveLibraryItem(libraryItem)
			.subscribe(
				(libraryItem) => {
					vm.chartViewModel.data.uuid = libraryItem.uuid;

					vm.createModel(vm.chartViewModel.data);

					let newStartRuleDataModel = {
						description: "START",
						id: 0,
						layout: {
							x: -162,
							y: 25
						},
						onPass: {
							action: "",
							ruleId: <any>[]
						},
						onFail: {
							action: "",
							ruleId: <any>[]
						}
					};

					vm.chartViewModel.addRule(newStartRuleDataModel);

					vm.logger.success('Cohort saved successfully', libraryItem, 'Saved');

					if (close) {

						vm.state.go(vm.transition.from());
					}
				},
				(error) => vm.logger.error('Error saving cohort', error, 'Error')
			);
	}

	onRuleDescription($event) {
		let description = $event.description;
		if (description === "START") {
			this.disableRuleProps = true;
		}
		else {
			this.disableRuleProps = false;
		}
		this.ruleDescription = description;
	}

	onRulePassAction($event) {
		this.rulePassAction = $event.action;
	}

	onRuleFailAction($event) {
		this.ruleFailAction = $event.action;
	}

	onEditTest($event) {
		let ruleId = $event.ruleId;
		let vm = this;
		if (ruleId !== "0") {
			vm.ruleId = ruleId;

			let selectedRule = vm.chartViewModel.getSelectedRule();
			let rules = <any>[];

			for (let i = 0; i < vm.chartViewModel.data.query.rule.length; ++i) {
				if (vm.chartViewModel.data.query.rule[i].description !== "START"
					&& !vm.chartViewModel.data.query.rule[i].expression
					&& vm.chartViewModel.data.query.rule[i].id!=vm.ruleId) {
					let rule = {
						value: vm.chartViewModel.data.query.rule[i].id,
						displayName: vm.chartViewModel.data.query.rule[i].description
					};
					rules.push(rule);
				}
			}

			if (selectedRule.data.expression) {
				let expression: ExpressionType = selectedRule.data.expression;

				ExpressionEditDialog.open(vm.$modal, expression, rules)
					.result.then(function (resultData: ExpressionType) {

					selectedRule.data.expression = resultData;
				});
			}
			else if (!selectedRule.data.queryLibraryItemUUID) {
				let test: Test = selectedRule.data.test;
				let originalResultData = jQuery.extend(true, {}, test);

				TestEditDialog.open(vm.$modal, originalResultData, selectedRule.data.type, rules)
					.result.then(function (resultData: Test) {

					selectedRule.data.test = resultData;

					if (vm.ruleDescription=="Feature Description") {
						vm.ruleDescription = selectedRule.data.test.filter[0].codeSet.codeSetValue[0].term;
						vm.ruleDescriptionChange();
					}
				});
			}
		}
	}
}