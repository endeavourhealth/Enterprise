/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../models/MenuOption.ts" />
/// <reference path="../models/Role.ts" />
/// <reference path="../models/User.ts" />
/// <reference path="../models/UserInRole.ts" />

module app.core {
	'use strict';

	export interface IAdminService {
		getCurrentUser() : app.models.User;
		switchUserInRole(userInRoleUuid:string);
		getMenuOptions() : app.models.MenuOption[];
		isAuthenticated() : boolean;
		login(username:string, password:string);
		logout();
	}

	export class AdminService implements IAdminService {
		static $inject = ['$http', '$q'];
		currentUser:app.models.User;

		constructor(private http:ng.IHttpService, private promise:ng.IQService) {
			this.currentUser = null;
		}

		getCurrentUser() : app.models.User {
			return this.currentUser;
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
			return this.currentUser != null;
		}

		login(username:string, password:string) {
			var vm = this;
			vm.currentUser = null;
			var defer = vm.promise.defer();
			var request = {
				'username': username,
				'password': password
			};
			vm.http.post('/api/security/login', request)
				.then(function (response) {
					vm.currentUser = <app.models.User>response.data;
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		switchUserInRole(userInRoleUuid:string) {
			var vm = this;
			var defer = vm.promise.defer();
			var request = '"' + userInRoleUuid + '"';
			vm.http.post('/api/security/switchUserInRole', request)
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}

		logout() {
			this.currentUser = null;
		}
	}

	angular
		.module('app.core')
		.service('AdminService', AdminService);
}