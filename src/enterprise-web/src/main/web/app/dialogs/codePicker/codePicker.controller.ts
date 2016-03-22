/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import TermlexSearchResult = app.models.TermlexSearchResult;
	import TermlexSearchResultResult = app.models.TermlexSearchResultResult;
	import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import IModalService = angular.ui.bootstrap.IModalService;
	import Code = app.models.Code;
	import CodeSelection = app.models.CodeSelection;
	import ExclusionTreeNode = app.models.ExclusionTreeNode;
	import ICodingService = app.core.ICodingService;
	'use strict';

	export class CodePickerController extends BaseDialogController {
		selectedMatch : Code;
		previousSelection : Code;
		selectedExclusion : CodeSelection;

		searchData : string;
		searchResults : Code[];
		parents : Code[];
		children : Code[];

		exclusionTreeData : ExclusionTreeNode[];

		public static open($modal : IModalService, selection : CodeSelection[]) : IModalServiceInstance {
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
								private selection : CodeSelection[]) {
			super($uibModalInstance);
			this.searchData = 'Asthma';
			this.resultData = selection;
		}

		search() {
			var vm = this;
			//vm.searchResults = vm.termlexSearch.getFindings(vm.searchData, vm.searchOptions);
			vm.codingService.searchCodes(vm.searchData)
				.then(function(result:TermlexSearchResult) {
					vm.searchResults = result.results;
				});
		}

		displayCode(itemToDisplay : Code, replace : boolean) {
			var vm = this;

			if (vm.selectedMatch) {
				vm.previousSelection = vm.selectedMatch;
			}

			if (replace) {
				vm.searchResults = [itemToDisplay];
			}

			vm.codingService.getCodeChildren(itemToDisplay.id)
				.then(function(result:Code[]) {
					vm.children = result;
				});

			vm.codingService.getCodeParents(itemToDisplay.id)
				.then(function(result:Code[]) {
					vm.parents = result;
				});

			vm.selectedMatch = itemToDisplay;
		}

		select(match : Code) {
			var item : CodeSelection = {
				id : match.id,
				label : match.label,
				includeChildren : true,
				exclusions : []
			};
			this.resultData.push(item);
		}

		unselect(item : CodeSelection) {
			var i = this.resultData.indexOf(item);
			if (i !== -1) {
				this.resultData.splice(i, 1);
			}
		}

		displayExclusionTree(selection : CodeSelection) {
			var vm = this;
			vm.selectedExclusion = selection;

			vm.codingService.getCodeChildren(selection.id)
				.then(function(result:Code[]) {
					var exclusionTreeNode : ExclusionTreeNode = selection as ExclusionTreeNode;
					exclusionTreeNode.children = result as ExclusionTreeNode[];
					exclusionTreeNode.children.forEach((item) => {
						// If "Top-level include"
						if (exclusionTreeNode.includeChildren) {
							// and no "excludes" then tick
							if (exclusionTreeNode.exclusions.length && exclusionTreeNode.exclusions.length === 0) {
								item.includeChildren = true;
							} else {
								// else if this is not excluded then tick
								item.includeChildren = exclusionTreeNode.exclusions.every((exclusion) => {
									return exclusion.id !== item.id;
								});
							}
						}
					});
					vm.exclusionTreeData = [ exclusionTreeNode ];
				});
		}

		includeNode(node : ExclusionTreeNode) {
			if (node.id === this.selectedExclusion.id) {
				this.selectedExclusion.exclusions = [];
				this.selectedExclusion.includeChildren = true;
				node.children.forEach((item) => { item.includeChildren = true; });
			} else {
				if (this.selectedExclusion.includeChildren) {
					var index = this.findWithAttr(this.selectedExclusion.exclusions, 'id', node.id);
					if (index > -1) {
						this.selectedExclusion.exclusions.splice(index, 1);
						node.includeChildren = true;
						if (this.selectedExclusion.exclusions.length === 0) {
							this.selectedExclusion.includeChildren = true;
						}
					}
				} else {
					this.selectedExclusion.includeChildren = true;
					this.selectedExclusion.exclusions = this.exclusionTreeData[0].children.slice(0);
					this.includeNode(node);
				}
			}
		}

		excludeNode(node : ExclusionTreeNode) {
			if (node.id === this.selectedExclusion.id) {
				this.selectedExclusion.exclusions = [];
				this.selectedExclusion.includeChildren = false;
				node.children.forEach((item) => { item.includeChildren = false; });
			} else {
				this.selectedExclusion.exclusions.push(node);
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
