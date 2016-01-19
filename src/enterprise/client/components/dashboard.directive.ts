/// <reference path="../typings/angularjs/angular.d.ts" />
module Dashboard
{
    export class DashboardDirective implements ng.IDirective
    {
        public restrict: string = "E";
        public replace: boolean = true;
        public templateUrl: string = "modules/dashboard/dashboard.html";
        public controller: string = 'DashboardController';
        public controllerAs: string = 'dashboard';
        public scope = {};
    }

    angular
        .module('Dashboard')
        .directive("dashboard", [() => new Dashboard.DashboardDirective()]);
}