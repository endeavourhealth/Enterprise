/// <reference path="../../../typings/tsd.d.ts" />

angular.module('app.blocks.router', ['ui.router'])
	.run(['$state', function ($state) {
		$state.go('dashboard', {}, {reload: true});
	}]);