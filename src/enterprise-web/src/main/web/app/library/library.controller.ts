/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.library {
	import FolderNode = app.models.FolderNode;
	import ItemSummaryList = app.models.ItemSummaryList;
	import FolderContent = app.models.FolderItem;
	import ILoggerService = app.blocks.ILoggerService;
	import itemTypeIdToString = app.models.itemTypeIdToString;
	import IModalService = angular.ui.bootstrap.IModalService;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import InputBoxController = app.dialogs.InputBoxController;
	import CodePickerController = app.dialogs.CodePickerController;
	import IScope = angular.IScope;
	import TermlexCode = app.models.Code;
	import TermlexCodeSelection = app.models.CodeSelection;
	import Folder = app.models.Folder;
	import FolderType = app.models.FolderType;
	import MessageBoxController = app.dialogs.MessageBoxController;
	import FolderItem = app.models.FolderItem;
	import ItemType = app.models.ItemType;
	import LibraryItem = app.models.LibraryItem;
	import CodeSetValue = app.models.CodeSetValue;
	import IFolderService = app.core.IFolderService;
	import ILibraryService = app.core.ILibraryService;
	import LibraryItemFolderModuleBase = app.blocks.LibraryItemFolderModuleBase;
	'use strict';

	export class LibraryController extends LibraryItemFolderModuleBase {
		selectedNode : FolderNode = null;

		static $inject = ['LibraryService', 'FolderService', 'LoggerService', '$scope', '$uibModal', '$state'];

		constructor(
			protected libraryService:ILibraryService,
			protected folderService:IFolderService,
			protected logger:ILoggerService,
			protected $scope : IScope,
			protected $modal : IModalService,
			protected $state : IStateService) {
			super(logger, $modal, folderService, FolderType.Library);
		}

		actionItem(item : FolderItem, action : string) {
			switch (item.type) {
				case ItemType.Query:
					this.$state.go('app.queryAction', {itemUuid: item.uuid, itemAction: action});
					break;
				case ItemType.ListOutput:
					this.$state.go('app.listOutputAction', {itemUuid: item.uuid, itemAction: action});
					break;
				case ItemType.CodeSet:
					this.$state.go('app.codeSetAction', {itemUuid: item.uuid, itemAction: action});
					break;
			}
		}
	}

	angular
		.module('app.library')
		.controller('LibraryController', LibraryController);
}
