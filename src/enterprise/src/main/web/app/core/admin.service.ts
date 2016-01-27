/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../models/MenuOption.ts" />
/// <reference path="../models/Role.ts" />
/// <reference path="../models/User.ts" />
/// <reference path="../models/UserInRole.ts" />

module app.core {
	'use strict';

	export interface IAdminService {
		getCurrentUser() : ng.IPromise<app.models.User>;
		getMenuOptions() : app.models.MenuOption[];
	}

	export class AdminService implements IAdminService {
		static $inject = ["$http", "$q"];

		constructor(private http:ng.IHttpService, private promise:ng.IQService) {
		}

		getCurrentUser():ng.IPromise<app.models.User> {
			var defer = this.promise.defer();
			this.http.get("/api/user")
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		getMenuOptions():app.models.MenuOption[] {
			return [
				{caption: "Dashboard", state: "dashboard", icon: "glyphicon-dashboard"},
				{caption: "Library", state: "library", icon: "glyphicon-book"},
				{caption: "Reports", state: "reports", icon: "glyphicon-file"},
				{caption: "Administration", state: "admin", icon: "glyphicon-cog"},
				{caption: "Audit", state: "audit", icon: "glyphicon-check"}
			];
		}
	}

	angular
		.module("app.core")
		.service("AdminService", AdminService);
}