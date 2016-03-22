/// <reference path="../../typings/tsd.d.ts" />

module app.core {
	import ItemSummaryList = app.models.ItemSummaryList;
	import FolderNode = app.models.FolderNode;
	import Report = app.models.Report;
	import TermlexCode = app.models.Code;
	import Folder = app.models.Folder;
	import UuidNameKVP = app.models.UuidNameKVP;
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
		getLibraryItemNamesForReport(uuid : string):ng.IPromise<{contents : UuidNameKVP[]}>;
		saveFolder(folder : Folder):ng.IPromise<string>;
		deleteFolder(folder : Folder):ng.IPromise<any>;
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

		getReport(uuid : string):ng.IPromise<Report> {
			var request = {
				params: {
					'uuid': uuid
				}
			};
			return this.httpGet('api/report/getReport', request);
		}

		getLibraryItemNamesForReport(uuid : string):ng.IPromise<{contents : UuidNameKVP[]}> {
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
	}

	angular
		.module('app.core')
		.service('LibraryService', LibraryService);
}