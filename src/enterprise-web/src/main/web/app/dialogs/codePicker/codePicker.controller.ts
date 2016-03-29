/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import IModalService = angular.ui.bootstrap.IModalService;
	import ExclusionTreeNode = app.models.ExclusionTreeNode;
	import ICodingService = app.core.ICodingService;
	import CodeSetValue = app.models.CodeSetValue;
	import Concept = app.models.Concept;

	'use strict';

	export class CodePickerController extends BaseDialogController {
		selectedMatch : CodeSetValue;
		previousSelection : CodeSetValue;
		selectedExclusion : CodeSetValue;

		searchData : string;
		searchResults : CodeSetValue[];
		parents : CodeSetValue[];
		children : CodeSetValue[];

		termCache : any;

		exclusionTreeData : ExclusionTreeNode[];

		public static open($modal : IModalService, selection : CodeSetValue[]) : IModalServiceInstance {
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
								private selection : CodeSetValue[]) {
			super($uibModalInstance);
			this.termCache = {};
			this.resultData = this.cloneCodeSetValueList(selection);
		}

		cloneCodeSetValueList(source : CodeSetValue[]) {
			if (source == null) { return null; }

			var target : CodeSetValue[] = [];
			for (var i = 0; i < source.length; i++) {
				var clone : CodeSetValue = {
					code : source[i].code,
					includeChildren : source[i].includeChildren,
					exclusion : this.cloneCodeSetValueList(source[i].exclusion)
				};
				target.push(clone);
			}
			return target;
		}

		search() {
			var vm = this;
			//vm.searchResults = vm.termlexSearch.getFindings(vm.searchData, vm.searchOptions);
			vm.codingService.searchCodes(vm.searchData)
				.then(function(result:CodeSetValue[]) {
					vm.searchResults = result;
					vm.parents = [];
					vm.children = [];
				});
		}

		displayCode(itemToDisplay : CodeSetValue, replace : boolean) {
			var vm = this;

			if (vm.selectedMatch) {
				vm.previousSelection = vm.selectedMatch;
			}

			if (replace) {
				vm.searchResults = [itemToDisplay];
			}

			vm.codingService.getCodeChildren(itemToDisplay.code)
				.then(function(result:CodeSetValue[]) {
					vm.children = result;
				});

			vm.codingService.getCodeParents(itemToDisplay.code)
				.then(function(result:CodeSetValue[]) {
					vm.parents = result;
				});

			vm.selectedMatch = itemToDisplay;
		}

		select(match : CodeSetValue) {
			var item : CodeSetValue = {
				code : match.code,
				includeChildren : true,
				exclusion : []
			};
			this.resultData.push(item);
		}

		unselect(item : CodeSetValue) {
			var i = this.resultData.indexOf(item);
			if (i !== -1) {
				this.resultData.splice(i, 1);
			}
		}

		displayExclusionTree(selection : CodeSetValue) {
			var vm = this;
			vm.selectedExclusion = selection;

			vm.codingService.getCodeChildren(selection.code)
				.then(function(result:CodeSetValue[]) {
					var exclusionTreeNode : ExclusionTreeNode = selection as ExclusionTreeNode;
					exclusionTreeNode.children = result as ExclusionTreeNode[];
					exclusionTreeNode.children.forEach((item) => {
						// If "Top-level include"
						if (exclusionTreeNode.includeChildren) {
							// and no "excludes" then tick
							if ((!exclusionTreeNode.exclusion) || exclusionTreeNode.exclusion.length === 0) {
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

		getTerm(code : string) : string {
			var vm = this;
			var term = vm.termCache[code];
			if (term) { return term; }
			vm.termCache[code] = 'Loading...';

			vm.codingService.getPreferredTerm(code)
				.then(function(concept : Concept) {
					vm.termCache[code] = concept.preferredTerm;
				});

			return vm.termCache[code];
		}
	}

	angular
		.module('app.dialogs')
		.controller('CodePickerController', CodePickerController);
}
