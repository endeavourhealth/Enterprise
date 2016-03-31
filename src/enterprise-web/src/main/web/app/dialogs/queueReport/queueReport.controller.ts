/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import IModalService = angular.ui.bootstrap.IModalService;
	import Report = app.models.Report;
	import RequestParameters = app.models.RequestParameters;

	'use strict';

	export class QueueReportController extends BaseDialogController {
		public static open($modal : IModalService, reportUuid : string, reportName : string) : IModalServiceInstance {
			var options : IModalSettings = {
				templateUrl:'app/dialogs/queueReport/queueReport.html',
				controller:'QueueReportController',
				controllerAs:'queueReport',
				// size:'lg',
				backdrop: 'static',
				resolve:{
					reportUuid : () => reportUuid,
					reportName : () => reportName
				}
			};

			var dialog = $modal.open(options);
			return dialog;
		}

		patientTypeDisplay : any;
		patientStatusDisplay : any;
		baselineDate : Date;

		static $inject = ['$uibModalInstance', 'LoggerService', 'AdminService', 'reportUuid', 'reportName'];

		constructor(protected $uibModalInstance : IModalServiceInstance,
								private logger:app.blocks.ILoggerService,
								private adminService : IAdminService,
								private reportUuid : string,
								private reportName : string) {
			super($uibModalInstance);

			this.patientTypeDisplay = {
				regular : 'Regular patients',
				nonRegular : 'Non-regular patients',
				all : 'All patients'
			};

			this.patientStatusDisplay = {
				active : 'Active patients',
				all : 'Active and non-active patients'
			};

			var requestParameters:RequestParameters = {
				reportUuid: reportUuid,
				baselineDate: null,
				patientType: 'regular',
				patientStatus: 'active',
				organisation: null
			};

			this.resultData = requestParameters;
		}

		ok() {
			if (this.baselineDate) {
				this.resultData.baselineDate = this.baselineDate.valueOf();
			} else {
				this.resultData.baselineDate = null;
			}

			super.ok();
		}
	}

	angular
		.module('app.dialogs')
		.controller('QueueReportController', QueueReportController);
}
