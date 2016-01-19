/// <reference path="../typings/angularjs/angular.d.ts" />
module SideBar
{
    export class SideBarDirective implements ng.IDirective
    {
        public restrict: string = "E";
        public replace: boolean = true;
        public templateUrl: string = "modules/sidebar/sidebar.html";
        //public controller: string = 'SideBarController';
        //public controllerAs: string = 'sidebar';
        public scope = {};
    }

    angular
        .module('SideBar')
        .directive("sidebar", [() => new SideBar.SideBarDirective()]);
}