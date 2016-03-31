/// <reference path="../../../typings/tsd.d.ts" />
/// <reference path="../../blocks/logger.service.ts" />

module app.dialogs {
	import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import IModalService = angular.ui.bootstrap.IModalService;
	import User = app.models.User;

	'use strict';

	export class UserEditorController extends BaseDialogController {
		public static open($modal : IModalService, user : User, readOnly : boolean) : IModalServiceInstance {
			var options : IModalSettings = {
				templateUrl:'app/dialogs/userEditor/userEditor.html',
				controller:'UserEditorController',
				controllerAs:'userEditor',
				backdrop: 'static',
				resolve:{
					user : () => user,
					readOnly : () => readOnly

				}
			};

			var dialog = $modal.open(options);
			return dialog;
		}

		static $inject = ['$uibModalInstance', 'LoggerService', 'AdminService', 'user', 'readOnly'];

		constructor(protected $uibModalInstance : IModalServiceInstance,
								private logger:app.blocks.ILoggerService,
								private adminService : IAdminService,
								private user : User,
								private readOnly : boolean) {
			super($uibModalInstance);
			this.resultData = jQuery.extend(true, {}, user);
		}
	}

	angular
		.module('app.dialogs')
		.controller('UserEditorController', UserEditorController);
}
