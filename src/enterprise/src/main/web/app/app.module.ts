/// <reference path="../typings/tsd.d.ts" />

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
	.run(['$state', 'Idle', '$rootScope', 'AdminService', function ($state, Idle, $rootScope, adminService) {
		$rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
			if (toState.unsecured !== true && !adminService.isAuthenticated()) {
				event.preventDefault();
				// $state.transitionTo('unauthorised');
			}
		});
		$state.go('login', {}, {reload: true});
		Idle.watch();
	}]);


