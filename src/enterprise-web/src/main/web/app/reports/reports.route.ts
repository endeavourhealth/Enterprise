/// <reference path="../../typings/tsd.d.ts" />

module app.reports {
	'use strict';

	class ReportsRoute {
		static $inject = ['$stateProvider'];

		constructor(stateProvider:angular.ui.IStateProvider) {
			var routes = ReportsRoute.getRoutes();

			routes.forEach(function (route) {
				stateProvider.state(route.state, route.config);
			});
		}

		static getRoutes() {
			return [
				{
					state: 'app.reports',
					config: {
						url: '/reports',
						templateUrl: 'app/reports/reports.html',
						controller: 'ReportsController',
						controllerAs: 'reports'
					}
				}
			];
		}
	}

	angular
		.module('app.reports')
		.config(ReportsRoute);

}