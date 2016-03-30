/// <reference path="../../typings/tsd.d.ts" />

module app.core {
	import Report = app.models.Report;
	import RequestParameters = app.models.RequestParameters;
	import UuidNameKVP = app.models.UuidNameKVP;
	'use strict';

	export interface IReportService {
		saveReport(report : Report):ng.IPromise<Report>;
		getReport(uuid : string):ng.IPromise<Report>;
		deleteReport(report : Report):ng.IPromise<any>;
		scheduleReport(requestParameters : RequestParameters):ng.IPromise<any>;
		getContentNamesForReportLibraryItem(uuid : string):ng.IPromise<{contents : UuidNameKVP[]}>;
	}

	export class ReportService extends BaseHttpService implements IReportService {
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

		getContentNamesForReportLibraryItem(uuid : string):ng.IPromise<{contents : UuidNameKVP[]}> {
			var request = {
				params: {
					'uuid': uuid
				}
			};
			return this.httpGet('api/library/getLibraryItemNamesForReport', request);
		}

		scheduleReport(requestParameters : RequestParameters):ng.IPromise<any> {
			return this.httpPost('api/report/scheduleReport', requestParameters);
		}
	}

	angular
		.module('app.core')
		.service('ReportService', ReportService);
}