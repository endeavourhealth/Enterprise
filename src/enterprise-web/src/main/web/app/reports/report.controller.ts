/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.reports {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import ILoggerService = app.blocks.ILoggerService;
	import FolderContent = app.models.FolderItem;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import LibraryController = app.library.LibraryController;
	import Report = app.models.Report;
	import ICallbacks = AngularUITree.ICallbacks;
	import IEventInfo = AngularUITree.IEventInfo;
	import FolderItem = app.models.FolderItem;
	import ReportNode = app.models.ReportNode;
	'use strict';

	class ReportController extends LibraryController {
		treeData : FolderNode[];
		selectedNode : FolderNode = null;
		itemSummaryList : ItemSummaryList;
		reportData : Report;
		itemAction : string;
		itemUuid : string;
		reportTreeCallbackOptions : any;

		static $inject = ['LibraryService', 'LoggerService', '$scope', '$stateParams'];

		constructor(
			protected libraryService:app.core.ILibraryService,
			protected logger : ILoggerService,
			protected $scope : any,
			protected $stateParams : any) {
			super(libraryService, logger, $scope);
			this.itemAction = $stateParams.itemAction;
			this.itemUuid = $stateParams.itemUuid;
			this.reportTreeCallbackOptions = {dropped : this.reportTreeDroppedCallback };

			//this.reportData = {
			//		uuid: uuid.v4(),
			//		name: 'Report',
			//		nodes: []
			//};

			this.reportData = {"uuid":"2fe4448b-d6b9-48f7-a684-6feba9b004ec","name":"Report","children":[{"uuid":"2d0ad623-69db-42dc-a191-73ef5abe7f58","itemUuid":"ecb4497a-16a2-44c3-8b51-15cfc4bea9f5","name":"Asthmatics","type":2,"children":[{"uuid":"cbe78a5f-ce19-4ba0-86c3-19bd4bc9e693","itemUuid":"098be27b-1dd3-432f-9edd-1049dad4f7ac","name":"Sub Asthmatics","type":2,"children":[]}]},{"uuid":"b7911567-9a78-4dc2-935a-4551e46e261e","itemUuid":"55086fcb-d24f-4601-afd7-b7cae55426e4","name":"renamed query","type":2,"children":[]},{"uuid":"ec525c9f-d3c7-41a0-8301-ab4bdb683cf9","itemUuid":"d7219ff4-339f-4a54-9ddd-818a0e00ace9","name":"Diabetics","type":2,"children":[{"uuid":"8de8dcdc-bb90-4586-b094-86a4207fe20b","itemUuid":"5f7665f1-7970-4091-8bfb-6b281489565d","name":"Diabetic Indicators","type":5,"children":[]}]}]};
		}

		remove(scope:any) {
			scope.remove();
		}

		reportTreeDroppedCallback(eventInfo: IEventInfo) {
			eventInfo.source.cloneModel.children = [];
		}
	}

	angular
		.module('app.reports')
		.controller('ReportController', ReportController);
}
