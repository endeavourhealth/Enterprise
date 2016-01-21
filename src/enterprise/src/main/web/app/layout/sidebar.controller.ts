/// <reference path="../../typings/tsd.d.ts" />

module app.layout {
    class SidebarController {
        menuOptions;

        constructor () {
            this.menuOptions = [
                {Caption: "Dashboard", Url: "dashboard", Icon: "glyphicon-dashboard"},
                {Caption: "Library", Url: "library", Icon: "glyphicon-book"},
                {Caption: "Reports", Url: "reports", Icon: "glyphicon-file"},
                {Caption: "Administration", Url: "admin", Icon: "glyphicon-cog"},
                {Caption: "Audit", Url: "audit", Icon: "glyphicon-check"}
            ];
        }
    }

    angular.module('app.layout')
        .controller('SidebarController', SidebarController);
}