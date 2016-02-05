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

		toggle(node : any) {
			node.show = !node.show;
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

		data = [{
			'id': 1,
			'title': 'node1',
			'nodes': [
				{
					'id': 11,
					'title': 'node1.1',
					'nodes': [
						{
							'id': 111,
							'title': 'node1.1.1',
							'nodes': []
						}
					]
				},
				{
					'id': 12,
					'title': 'node1.2',
					'nodes': []
				}
			]
		}, {
			'id': 2,
			'title': 'node2',
			'nodrop': true, // An arbitrary property to check in custom template for nodrop-enabled
			'nodes': [
				{
					'id': 21,
					'title': 'node2.1',
					'nodes': []
				},
				{
					'id': 22,
					'title': 'node2.2',
					'nodes': []
				}
			]
		}, {
			'id': 3,
			'title': 'node3',
			'nodes': [
				{
					'id': 31,
					'title': 'node3.1',
					'nodes': []
				}
			]
		}];
	}

	angular
		.module('app.dialogs')
		.controller('CodePickerController', CodePickerController);
}
