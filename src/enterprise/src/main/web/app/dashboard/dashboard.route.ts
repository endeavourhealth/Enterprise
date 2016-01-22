/// <reference path="../../typings/tsd.d.ts" />

module app.dashboard {
	'use strict';

	class DashboardRoute {
		static $inject = ["$stateProvider"];

		constructor($stateProvider:angular.ui.IStateProvider) {
			$stateProvider
				.state("dashboard", {
					url: "/dashboard",
					templateUrl: "app/dashboard/dashboard.html",
					controller: "DashboardController",
					controllerAs: "dashboard"
				});
		}
	}

	angular
		.module('app.dashboard')
		.config(DashboardRoute);

}