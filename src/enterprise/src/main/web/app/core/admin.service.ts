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
		isAuthenticated() : boolean;
		login() : boolean;
		logout();
	}

	export class AdminService implements IAdminService {
		static $inject = ['$http', '$q'];
		authenticated:boolean;

		constructor(private http:ng.IHttpService, private promise:ng.IQService) {
			this.authenticated = false;
		}

		getCurrentUser():ng.IPromise<app.models.User> {
			var defer = this.promise.defer();
			this.http.get('/api/user')
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
				{caption: 'Dashboard', state: 'app.dashboard', icon: 'glyphicon-dashboard'},
				{caption: 'Library', state: 'app.library', icon: 'glyphicon-book'},
				{caption: 'Reports', state: 'app.reports', icon: 'glyphicon-file'},
				{caption: 'Administration', state: 'app.admin', icon: 'glyphicon-cog'},
				{caption: 'Audit', state: 'app.audit', icon: 'glyphicon-check'},
				{caption: 'Query Builder', state: 'app.query', icon: 'glyphicon-question-sign'}
			];
		}

		isAuthenticated():boolean {
			return this.authenticated;
		}

		login() {
			this.authenticated = true;
			return this.authenticated;
		}

		logout() {
			this.authenticated = false;
		}
	}

	angular
		.module('app.core')
		.service('AdminService', AdminService);
}