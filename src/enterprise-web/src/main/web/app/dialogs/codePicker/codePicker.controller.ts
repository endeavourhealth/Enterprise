/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import CodeSearchResult = app.models.CodeSearchResult;
	import CodeSearchMatch = app.models.CodeSearchMatch;
	import ITreeNode = AngularUITree.ITreeNode;
	import ILibraryService = app.core.ILibraryService;
	import LibraryService = app.core.LibraryService;
	import TermlexSearchResult = app.models.TermlexSearchResult;
	import TermlexSearchResultResult = app.models.TermlexSearchResultResult;
	import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
	'use strict';

	class CodePickerController {
		selectedMatch : any;
		searchData : string;
		searchResults : TermlexSearchResult;
		treeData : any[];
		selectedItems : CodeSearchResult[];
		showModal : boolean;

		static $inject = ['LoggerService', 'LibraryService', '$uibModalInstance'];

		constructor(private logger:app.blocks.ILoggerService,
								private libraryService : ILibraryService,
								private $uibModalInstance : IModalServiceInstance) {
			this.showModal = false;
			// this.searchData = '"Asthma","Angina","Diabetes","Hadache","Glaucoma","Ankle"';
			this.searchData = 'Asthma';
			this.selectedItems = [
				{
					term: 'asthma',
					matches: [
						{
							term: 'asthma',
							code: '195967001'
						}
					]
				},
				{
					term: 'angina',
					matches: [
						{
							term: 'angina',
							code: '194828000'
						}
					]
				},
				{
					term: 'diabetes',
					matches: [
						{
							term: 'diabetes mellitus',
							code: '73211009'
						}
					]
				}
			];
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
				.then(function(result:ITreeNode[]) {
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

		getButtonProperties(item : CodeSearchResult) {
			if (item.matches.length === 0) {
				return 'btn-danger';
			}
			if (item.matches.length > 1) {
				return 'btn-warning';
			}
			return 'btn-success';
		}

		toggleExpansion(node: any) {
			var vm = this;
			node.isExpanded = !node.isExpanded;
			if (node.isExpanded && node.childCount > 0 && !node.nodes) {
				vm.libraryService.getCodeTreeData(node.id)
					.then(function(result:ITreeNode[]) {
						node.nodes = result;
					});
			}
		}

		getMatchCount(item : CodeSearchResult) {
			return item.matches.length > 1 ? item.matches.length : null;
		}

		ok() {
			this.$uibModalInstance.close(this.selectedItems);
			console.log('OK Pressed');
		}

		cancel() {
			this.$uibModalInstance.dismiss('cancel');
			console.log('Cancel Pressed');
		}
	}

	angular
		.module('app.dialogs')
		.controller('CodePickerController', CodePickerController);
}
