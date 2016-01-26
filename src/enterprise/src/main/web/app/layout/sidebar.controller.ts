/// <reference path="../../typings/tsd.d.ts" />

module app.layout {
	class SidebarController {
		menuOptions;

		constructor() {
			this.menuOptions = [
				{Caption: "Dashboard", State: "dashboard", Icon: "glyphicon-dashboard"},
				{Caption: "Library", State: "library", Icon: "glyphicon-book"},
				{Caption: "Reports", State: "reports", Icon: "glyphicon-file"},
				{Caption: "Administration", State: "admin", Icon: "glyphicon-cog"},
				{Caption: "Audit", State: "audit", Icon: "glyphicon-check"}
			];
		}
	}

	angular.module('app.layout')
		.controller('SidebarController', SidebarController);
}