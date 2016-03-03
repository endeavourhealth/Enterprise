/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.reports {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import ILoggerService = app.blocks.ILoggerService;
	import FolderContent = app.models.FolderContent;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import LibraryController = app.library.LibraryController;
	import ReportNode = app.models.ReportNode;
	'use strict';

	class ReportController extends LibraryController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;
		reportData : ReportNode[];
		itemAction : string;
		itemUuid : string;

		static $inject = ['LibraryService', 'LoggerService', '$scope', '$stateParams'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger : ILoggerService,
			protected $scope : any,
			protected $stateParams : any) {
			super(libraryService, logger, $scope);
			this.itemAction = $stateParams.itemAction;
			this.itemUuid = $stateParams.itemUuid;

			this.reportData = [
				{
					itemName:'Diabetics',
					nodes: [
						{	itemName:'Over 50', nodes:[] },
						{	itemName:'Amputation', nodes:[] },
						{	itemName:'Decreasing BO', nodes:[] },
					]},
				{
					itemName:'Recent Diabetics',
					nodes: [
						{	itemName:'All medication', nodes:[] }
					]}
			];

		}
	}

	angular
		.module('app.reports')
		.controller('ReportController', ReportController);
}
