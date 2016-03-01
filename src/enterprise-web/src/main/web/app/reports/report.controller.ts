/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.reports {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import ILoggerService = app.blocks.ILoggerService;
	import FolderContent = app.models.FolderContent;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	'use strict';

	class ReportController {
		itemAction : string;
		itemUuid : string;

		static $inject = ['LibraryService', 'LoggerService', '$stateParams', '$scope'];

		constructor(
			private libraryService:app.core.ILibraryService,
			private logger : ILoggerService,
			private $stateParams : any,
			private $scope : any) {
			this.itemAction = $stateParams.itemAction;
			this.itemUuid = $stateParams.itemUuid;
		}
	}

	angular
		.module('app.reports')
		.controller('ReportController', ReportController);
}
