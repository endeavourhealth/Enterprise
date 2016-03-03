/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import CodeSearchResult = app.models.CodeSearchResult;
	import CodeSearchMatch = app.models.CodeSearchMatch;
	import ITreeNode = AngularUITree.ITreeNode;
	import ILibraryService = app.core.ILibraryService;
	import LibraryService = app.core.LibraryService;
	import IModalService = angular.ui.bootstrap.IModalService;
	import TermlexSearchResult = app.models.TermlexSearchResult;
	import TermlexSearchResultResult = app.models.TermlexSearchResultResult;
	'use strict';

	class CodePickerController {
		searchData : string;
		searchResults : TermlexSearchResult;
		treeData : ITreeNode[];
		selectedItems : CodeSearchResult[];
		showModal : boolean;

		static $inject = ['LoggerService', 'LibraryService', '$uibModal'];

		constructor(private logger:app.blocks.ILoggerService,
								private libraryService : ILibraryService,
								private $uibModal : IModalService) {
			this.showModal = false;
			// this.searchData = '"Asthma","Angina","Diabetes","Hadache","Glaucoma","Ankle"';
			this.searchData = 'Asthma';
		}

		open() {
			var vm = this;
			vm.$uibModal.open({
				animation : true,
				size : 'lg',
				templateUrl : 'myModalContent.html',
				controller : 'CodePickerController',
				controllerAs : 'codePicker'
			});
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
			vm.libraryService.getCodeTreeData(match.id)
				.then(function(result:ITreeNode[]) {
					vm.treeData = [
						{
							id: match.id,
							title: match.label,
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

		getMatchCount(item : CodeSearchResult) {
			return item.matches.length > 1 ? item.matches.length : null;
		}
	}

	angular
		.module('app.dialogs')
		.controller('CodePickerController', CodePickerController);
}
