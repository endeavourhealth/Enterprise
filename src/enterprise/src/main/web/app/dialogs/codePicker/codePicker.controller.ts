/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import CodeSearchResult = app.models.CodeSearchResult;
	import CodeSearchMatch = app.models.CodeSearchMatch;
	import ITreeNode = AngularUITree.ITreeNode;
	import ILibraryService = app.core.ILibraryService;
	import LibraryService = app.core.LibraryService;
	'use strict';

	class CodePickerController {
		searchData : string;
		searchResults : CodeSearchResult;
		treeData : ITreeNode[];
		selectedItems : CodeSearchResult[];

		static $inject = ['LoggerService', 'LibraryService'];

		constructor(private logger:app.blocks.ILoggerService, private libraryService : ILibraryService) {
			this.searchData = '"Asthma","Angina","Diabetes","Hadache","Glaucoma","Ankle"';
			logger.success('CodePicker constructed', 'CodePickerData', 'CodePicker');
		}

		search() {
			var vm = this;
			vm.libraryService.searchCodes(vm.searchData)
				.then(function(result:CodeSearchResult[]) {
					vm.selectedItems = result;
					if (result.length === 1) {
						vm.searchResults = result[0];
					} else {
						vm.searchResults = null;
					}
				});
		}

		getTreeData(match : CodeSearchMatch) {
			var vm = this;
			vm.libraryService.getTreeData(match.code)
				.then(function(result:ITreeNode[]) {
					vm.treeData = result;
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
