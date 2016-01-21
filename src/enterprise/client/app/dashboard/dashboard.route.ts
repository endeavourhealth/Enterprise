/// <reference path="../../typings/tsd.d.ts" />

module app.dashboard {
    'use strict';

    class DashboardRoute {
        static $inject = ["$routeProvider"];
        constructor($routeProvider : ng.route.IRouteProvider) {
            $routeProvider
                .when("/", {
                    templateUrl: "app/dashboard/dashboard.html",
                    controller: "DashboardController",
                    controllerAs: "dashboard"
                })
                .when("/dashboard", {
                    templateUrl: "app/dashboard/dashboard.html",
                    controller: "DashboardController",
                    controllerAs: "dashboard"
                })
                .otherwise({ redirectTo: "/"});
        }
    }

    angular
        .module('app.dashboard')
        .config(DashboardRoute);

}