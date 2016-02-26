/// <reference path="../../../typings/tsd.d.ts" />

module app.dialogs {
	'use strict';

	class CodePickerRoute {
		static $inject = ['$stateProvider'];

		constructor(stateProvider:angular.ui.IStateProvider) {
			var routes = CodePickerRoute.getRoutes();

			routes.forEach(function (route) {
				stateProvider.state(route.state, route.config);
			});
		}

		static getRoutes() {
			return [
				{
					state: 'app.codePicker',
					config: {
						url: '/codePicker',
						templateUrl: 'app/dialogs/codePicker/codePicker.html',
						controller: 'CodePickerController',
						controllerAs: 'codePicker'
					}
				}
			];
		}
	}

	angular
		.module('app.dialogs')
		.config(CodePickerRoute);

}