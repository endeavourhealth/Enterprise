/// <reference path="../typings/tsd.d.ts" />

import ILoggerService = app.blocks.ILoggerService;
import IStateService = angular.ui.IStateService;
import IRootScopeService = angular.IRootScopeService;
import IAdminService = app.core.IAdminService;

angular.module('app', [
		'ui.bootstrap',
		'ngIdle',

		'app.core',
		'app.blocks',
		'app.layout',
		'app.login',

		'app.dialogs',
		'app.dashboard',
		'app.library',
		'app.admin',
		'app.query',
		'flowChart',
		'dragging',
		'mouseCapture'

	])
	.run(['$state', '$rootScope', 'AdminService', 'LoggerService',
		function ($state:IStateService, $rootScope:IRootScopeService, adminService:IAdminService, logger:ILoggerService) {
			$rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
				if (toState.unsecured !== true && !adminService.isAuthenticated()) {
					logger.error('You are not logged in');
					event.preventDefault();
					$state.transitionTo('login');
				}
			});
			$state.go('login', {}, {reload: true});
		}]
	);


