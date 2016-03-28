/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import IModalService = angular.ui.bootstrap.IModalService;
	import ExclusionTreeNode = app.models.ExclusionTreeNode;
	import ICodingService = app.core.ICodingService;
	import CodeSetValue = app.models.CodeSetValue;
	import CodeSetValueWithTerm = app.models.CodeSetValueWithTerm;

	'use strict';

	export class CodePickerController extends BaseDialogController {
		selectedMatch : CodeSetValueWithTerm;
		previousSelection : CodeSetValueWithTerm;
		selectedExclusion : CodeSetValueWithTerm;

		searchData : string;
		searchResults : CodeSetValueWithTerm[];
		parents : CodeSetValueWithTerm[];
		children : CodeSetValueWithTerm[];

		exclusionTreeData : ExclusionTreeNode[];

		public static open($modal : IModalService, selection : CodeSetValueWithTerm[]) : IModalServiceInstance {
			var options : IModalSettings = {
				templateUrl:'app/dialogs/codePicker/codePicker.html',
				controller:'CodePickerController',
				controllerAs:'codePicker',
				size:'lg',
				backdrop: 'static',
				resolve:{
					selection : () => selection
				}
			};

			var dialog = $modal.open(options);
			return dialog;
		}

		static $inject = ['$uibModalInstance', 'LoggerService', 'CodingService', 'selection'];

		constructor(protected $uibModalInstance : IModalServiceInstance,
								private logger:app.blocks.ILoggerService,
								private codingService : ICodingService,
								private selection : CodeSetValueWithTerm[]) {
			super($uibModalInstance);
			this.searchData = 'Asthma';
			this.resultData = selection;
		}

		search() {
			var vm = this;
			//vm.searchResults = vm.termlexSearch.getFindings(vm.searchData, vm.searchOptions);
			vm.codingService.searchCodes(vm.searchData)
				.then(function(result:CodeSetValueWithTerm[]) {
					vm.searchResults = result;
				});
		}

		displayCode(itemToDisplay : CodeSetValueWithTerm, replace : boolean) {
			var vm = this;

			if (vm.selectedMatch) {
				vm.previousSelection = vm.selectedMatch;
			}

			if (replace) {
				vm.searchResults = [itemToDisplay];
			}

			vm.codingService.getCodeChildren(itemToDisplay.code)
				.then(function(result:CodeSetValueWithTerm[]) {
					vm.children = result;
				});

			vm.codingService.getCodeParents(itemToDisplay.code)
				.then(function(result:CodeSetValueWithTerm[]) {
					vm.parents = result;
				});

			vm.selectedMatch = itemToDisplay;
		}

		select(match : CodeSetValueWithTerm) {
			var item : CodeSetValueWithTerm = {
				code : match.code,
				term : match.term,
				includeChildren : true,
				exclusion : []
			};
			this.resultData.push(item);
		}

		unselect(item : CodeSetValueWithTerm) {
			var i = this.resultData.indexOf(item);
			if (i !== -1) {
				this.resultData.splice(i, 1);
			}
		}

		displayExclusionTree(selection : CodeSetValueWithTerm) {
			var vm = this;
			vm.selectedExclusion = selection;

			vm.codingService.getCodeChildren(selection.code)
				.then(function(result:CodeSetValueWithTerm[]) {
					var exclusionTreeNode : ExclusionTreeNode = selection as ExclusionTreeNode;
					exclusionTreeNode.children = result as ExclusionTreeNode[];
					exclusionTreeNode.children.forEach((item) => {
						// If "Top-level include"
						if (exclusionTreeNode.includeChildren) {
							// and no "excludes" then tick
							if (exclusionTreeNode.exclusion.length && exclusionTreeNode.exclusion.length === 0) {
								item.includeChildren = true;
							} else {
								// else if this is not excluded then tick
								item.includeChildren = exclusionTreeNode.exclusion.every((exclusion) => {
									return exclusion.code !== item.code;
								});
							}
						}
					});
					vm.exclusionTreeData = [ exclusionTreeNode ];
				});
		}

		includeNode(node : ExclusionTreeNode) {
			if (node.code === this.selectedExclusion.code) {
				this.selectedExclusion.exclusion = [];
				this.selectedExclusion.includeChildren = true;
				node.children.forEach((item) => { item.includeChildren = true; });
			} else {
				if (this.selectedExclusion.includeChildren) {
					var index = this.findWithAttr(this.selectedExclusion.exclusion, 'code', node.code);
					if (index > -1) {
						this.selectedExclusion.exclusion.splice(index, 1);
						node.includeChildren = true;
						if (this.selectedExclusion.exclusion.length === 0) {
							this.selectedExclusion.includeChildren = true;
						}
					}
				} else {
					this.selectedExclusion.includeChildren = true;
					this.selectedExclusion.exclusion = this.exclusionTreeData[0].children.slice(0);
					this.includeNode(node);
				}
			}
		}

		excludeNode(node : ExclusionTreeNode) {
			if (node.code === this.selectedExclusion.code) {
				this.selectedExclusion.exclusion = [];
				this.selectedExclusion.includeChildren = false;
				node.children.forEach((item) => { item.includeChildren = false; });
			} else {
				this.selectedExclusion.exclusion.push(node);
				node.includeChildren = false;
			}
		}

		findWithAttr(array : any[], attr : string, value : string) : number {
			for (var i = 0; i < array.length; i += 1) {
				if (array[i][attr] === value) {
					return i;
				}
			}
			return -1;
		}
	}

	angular
		.module('app.dialogs')
		.controller('CodePickerController', CodePickerController);
}
