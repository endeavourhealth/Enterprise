/// <reference path="../../typings/tsd.d.ts" />

module app.core {
	import ItemSummaryList = app.models.ItemSummaryList;
	import FolderNode = app.models.FolderNode;
	import Report = app.models.Report;
	import TermlexCode = app.models.Code;
	import Folder = app.models.Folder;
	import UuidNameKVP = app.models.UuidNameKVP;
	import ListReport = app.models.ListReport;
	import LibraryItem = app.models.LibraryItem;
	import EntityMap = app.models.EntityMap;
	import RequestParameters = app.models.RequestParameters;
	'use strict';

	export interface ILibraryService {
		getEngineHistory() : ng.IPromise<app.models.EngineHistoryItem[]>;
		getRecentDocumentsData() : ng.IPromise<app.models.FolderItem[]>;
		getEngineState() : ng.IPromise<app.models.EngineState>;
		getReportActivityData() : ng.IPromise<app.models.ReportActivityItem[]>;
		getFolders(moduleId : number, folderUuid : string):ng.IPromise<{folders : FolderNode[]}>;
		getFolderContents(folderId : string):ng.IPromise<ItemSummaryList>;
		saveReport(report : Report):ng.IPromise<Report>;
		getReport(uuid : string):ng.IPromise<Report>;
		deleteReport(report : Report):ng.IPromise<any>;
		scheduleReport(requestParameters : RequestParameters):ng.IPromise<any>;
		getContentNamesForReportLibraryItem(uuid : string):ng.IPromise<{contents : UuidNameKVP[]}>;
		saveFolder(folder : Folder):ng.IPromise<string>;
		deleteFolder(folder : Folder):ng.IPromise<any>;
		getLibraryItem(uuid : string):ng.IPromise<LibraryItem>;
		saveLibraryItem(libraryItem : LibraryItem):ng.IPromise<LibraryItem>;
		deleteLibraryItem(libraryItem : LibraryItem):ng.IPromise<any>;
		getEntityMap():ng.IPromise<EntityMap>;
	}

	export class LibraryService extends BaseHttpService implements ILibraryService {

		getEngineHistory():ng.IPromise<app.models.EngineHistoryItem[]> {
			var request = {
				params: {
					'count': 3
				}
			};

			return this.httpGet('api/dashboard/getEngineHistory', request);
		}

		getRecentDocumentsData():ng.IPromise<app.models.FolderItem[]> {
			var request = {
				params: {
					'count': 3
				}
			};

			return this.httpGet('api/dashboard/getRecentDocuments', request);
		}

		getEngineState():ng.IPromise<app.models.EngineState> {
			return this.httpGet('app/core/data/enginestate.json');
		}

		getReportActivityData():ng.IPromise<app.models.ReportActivityItem[]> {
			var request = {
				params: {
					'count': 3
				}
			};

			return this.httpGet('api/dashboard/getReportActivity', request);
		}

		getFolders(moduleId : number, folderUuid : string):ng.IPromise<{folders : FolderNode[]}> {
			var request = {
				params: {
					'folderType': moduleId,
					'parentUuid': folderUuid
				}
			};

			return this.httpGet('api/folder/getFolders', request);
		}

		getFolderContents(folderUuid : string):ng.IPromise<ItemSummaryList> {
			var request = {
				params: {
					'folderUuid': folderUuid
				}
			};
			return this.httpGet('api/folder/getFolderContents', request);
		}

		saveReport(report: Report):ng.IPromise<Report> {
			return this.httpPost('api/report/saveReport', report);
		}

		deleteReport(report: Report):ng.IPromise<any> {
			return this.httpPost('api/report/deleteReport', report);
		}

		getReport(uuid : string):ng.IPromise<Report> {
			var request = {
				params: {
					'uuid': uuid
				}
			};
			return this.httpGet('api/report/getReport', request);
		}

		scheduleReport(requestParameters : RequestParameters):ng.IPromise<any> {
			return this.httpPost('api/report/scheduleReport', requestParameters);
		}

		getContentNamesForReportLibraryItem(uuid : string):ng.IPromise<{contents : UuidNameKVP[]}> {
			var request = {
				params: {
					'uuid': uuid
				}
			};
			return this.httpGet('api/library/getLibraryItemNamesForReport', request);
		}

		saveFolder(folder: Folder):ng.IPromise<string> {
			// Make clean copy of object, just in case of additions
			// Typing the request ensures any property changes are caught
			var request : Folder = {
				uuid : folder.uuid,
				folderName : folder.folderName,
				folderType : folder.folderType,
				parentFolderUuid : folder.parentFolderUuid,
				hasChildren : folder.hasChildren,
				contentCount : folder.contentCount
			};

			return this.httpPost('api/folder/saveFolder', request);
		}

		deleteFolder(folder: Folder):ng.IPromise<string> {
			// Make clean copy of object, just in case of additions
			// Typing the request ensures any property changes are caught
			var request : Folder = {
				uuid : folder.uuid,
				folderName : folder.folderName,
				folderType : folder.folderType,
				parentFolderUuid : folder.parentFolderUuid,
				hasChildren : folder.hasChildren,
				contentCount : folder.contentCount
			};

			return this.httpPost('api/folder/deleteFolder', request);
		}

		getLibraryItem(uuid : string):ng.IPromise<LibraryItem> {
			var request = {
				params: {
					'uuid': uuid
				}
			};
			return this.httpGet('api/library/getLibraryItem', request);
		}

		saveLibraryItem(libraryItem : LibraryItem):ng.IPromise<LibraryItem> {
			return this.httpPost('api/library/saveLibraryItem', libraryItem);
		}

		deleteLibraryItem(libraryItem : LibraryItem):ng.IPromise<any> {
			return this.httpPost('api/library/deleteLibraryItem', libraryItem);
		}

		getEntityMap():ng.IPromise<EntityMap> {
			return this.httpGet('api/entity/getEntityMap');
		}
	}

	angular
		.module('app.core')
		.service('LibraryService', LibraryService);
}