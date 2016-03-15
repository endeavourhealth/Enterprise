/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import ITreeNode = AngularUITree.ITreeNode;
	import ILibraryService = app.core.ILibraryService;
	import LibraryService = app.core.LibraryService;
	import TermlexSearchResult = app.models.TermlexSearchResult;
	import TermlexSearchResultResult = app.models.TermlexSearchResultResult;
	import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import IModalService = angular.ui.bootstrap.IModalService;
	import CodeSelection = app.models.CodeSelection;
	import TermlexTreeNode = app.models.TermlexTreeNode;
	'use strict';

	export class CodePickerController extends BaseDialogController {
		selectedMatch : TermlexSearchResultResult;
		searchData : string;
		searchResults : TermlexSearchResult;
		treeData : TermlexTreeNode[];

		public static open($modal : IModalService, selection : CodeSelection[]) : IModalServiceInstance {
			var options : IModalSettings = {
				templateUrl:'app/dialogs/codePicker/codePicker.html',
				controller:'CodePickerController',
				controllerAs:'codePicker',
				size:'lg',
				resolve:{
					selection : () => selection
				}
			};

			var dialog = $modal.open(options);
			return dialog;
		}

		static $inject = ['$uibModalInstance', 'LoggerService', 'LibraryService', 'selection'];

		constructor(protected $uibModalInstance : IModalServiceInstance,
								private logger:app.blocks.ILoggerService,
								private libraryService : ILibraryService,
								private selection : CodeSelection[]) {
			super($uibModalInstance);
			this.searchData = 'Asthma';
			this.resultData = selection;
		}

		search() {
			var vm = this;
			//vm.searchResults = vm.termlexSearch.getFindings(vm.searchData, vm.searchOptions);
			vm.libraryService.searchCodes(vm.searchData)
				.then(function(result:TermlexSearchResult) {
					vm.searchResults = result;
				});
		}

		getCodeTreeData(match : TermlexSearchResultResult) {
			var vm = this;
			if (vm.selectedMatch) { vm.selectedMatch.isSelected = false; }
			match.isSelected = true;
			vm.selectedMatch = match;

			vm.libraryService.getCodeTreeData(match.id)
				.then(function(result:TermlexTreeNode[]) {
					vm.treeData = [
						{
							id: match.id,
							label: match.label,
							childCount: result.length,
							isExpanded: true,
							nodes: result
						}
					];
				});
		}

		getButtonProperties(item : CodeSelection) {
			if (item.matches.length === 0) {
				return 'btn-danger';
			}
			if (item.matches.length > 1) {
				return 'btn-warning';
			}
			return 'btn-success';
		}

		toggleExpansion(node: TermlexTreeNode) {
			var vm = this;
			node.isExpanded = !node.isExpanded;
			if (node.isExpanded && node.childCount > 0 && !node.nodes) {
				vm.libraryService.getCodeTreeData(node.id)
					.then(function(result:TermlexTreeNode[]) {
						node.nodes = result;
					});
			}
		}

		getMatchCount(item : CodeSelection) {
			return item.matches.length > 1 ? item.matches.length : null;
		}
	}

	angular
		.module('app.dialogs')
		.controller('CodePickerController', CodePickerController);
}
