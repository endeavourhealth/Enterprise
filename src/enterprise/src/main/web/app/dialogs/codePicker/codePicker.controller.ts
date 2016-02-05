/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import CodeSearchResult = app.models.CodeSearchResult;
	import CodeSearchMatch = app.models.CodeSearchMatch;
	'use strict';

	class CodePickerController {
		selectedItems : any;

		static $inject = ['LoggerService'];

		constructor(private logger:app.blocks.ILoggerService) {
			logger.success('CodePicker constructed', 'CodePickerData', 'CodePicker');
		}

		search() {
			this.selectedItems = this.getSelectedItems();
		}

		getButtonProperties(item : CodeSearchResult) {
			if (item.matches.length === 0) {
				return 'btn-danger disabled';
			}
			if (item.matches.length > 1) {
				return 'btn-warning';
			}
			return 'btn-success disabled';
		}

		getMatchCount(item : CodeSearchResult) {
			return item.matches.length > 1 ? item.matches.length : null;
		}

		getSelectedItems() : CodeSearchResult[] {
			return [
				{
					term: 'Asthma',
					matches: [
						{
							term: 'Asthma',
							code: 'H33'
						}
					]
				},
				{
					term: 'Angina',
					matches: [
						{
							term: 'Angina',
							code: 'H33'
						}
					]
				},
				{
					term: 'Diabetes',
					matches: [
						{
							term: 'Diabetes',
							code: 'H33'
						}
					]
				},
				{
					term: 'Hedche',
					matches: []
				}
				,
				{
					term: 'Glaucoma',
					matches: [
						{
							term: 'Glaucoma',
							code: 'H33'
						}
					]
				},
				{
					term: 'Ankle',
					matches: [
						{
							term: 'Broken ankle',
							code: 'ANKBRK'
						},
						{
							term: 'Fractured ankle',
							code: 'ANKFRC'
						},
						{
							term: 'Twisted ankle',
							code: 'ANKTWS'
						}
					]
				}
			]
				;
		}
	}

	angular
		.module('app.dialogs')
		.controller('CodePickerController', CodePickerController);
}
