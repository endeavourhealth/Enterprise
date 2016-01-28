/// <reference path="../typings/tsd.d.ts" />

angular.module('app', [
		'ui.bootstrap',
		'ngIdle',

		'app.core',
		'app.blocks',
		'app.layout',

		'app.dashboard',
		'app.library'
	])
	.run(['$state', 'Idle', function ($state, Idle) {
		$state.go('dashboard', {}, {reload: true});
		Idle.watch();
	}]);


