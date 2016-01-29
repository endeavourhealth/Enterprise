/// <reference path="../typings/tsd.d.ts" />

import ILoggerService = app.blocks.ILoggerService;
angular.module('app', [
		'ui.bootstrap',
		'ngIdle',

		'app.core',
		'app.blocks',
		'app.layout',
		'app.login',

		'app.dashboard',
		'app.library',
		'app.query'
	])
	.run(['$state', '$rootScope', 'AdminService', 'LoggerService',
		function ($state, $rootScope, adminService, logger:ILoggerService) {
			$rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
				if (toState.unsecured !== true && !adminService.isAuthenticated()) {
					logger.error('You are not logged in');
					event.preventDefault();
					$state.transitionTo('login');
				}
			});
			$state.go('login', {}, {reload: true});
		}]);


