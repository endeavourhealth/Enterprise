/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../models/MenuOption.ts" />
/// <reference path="../models/Role.ts" />
/// <reference path="../models/User.ts" />
/// <reference path="../models/UserInRole.ts" />

module app.core {
	import IPromise = angular.IPromise;
	import LoginResponse = app.models.LoginResponse;
	'use strict';

	export interface IAdminService {
		getMenuOptions() : app.models.MenuOption[];
		getUserList() : IPromise<app.models.UserList>;
		setPendingChanges() : void;
		clearPendingChanges() : void;
		getPendingChanges() : boolean;
	}

	export class AdminService extends BaseHttpService implements IAdminService {
		pendingChanges : boolean;
		currentUser:app.models.User;

		setPendingChanges() : void {
			this.pendingChanges = true;
		}

		clearPendingChanges() : void {
			this.pendingChanges = false;
		}

		getPendingChanges() : boolean {
			return this.pendingChanges;
		}

		getCurrentUser() : app.models.User {
			return this.currentUser;
		}

		getMenuOptions():app.models.MenuOption[] {
			return [
				{caption: 'Dashboard', state: 'app.dashboard', icon: 'glyphicon-dashboard'},
				{caption: 'Library', state: 'app.library', icon: 'glyphicon-book'},
				{caption: 'Reports', state: 'app.reportList', icon: 'glyphicon-file'},
				{caption: 'Administration', state: 'app.admin', icon: 'glyphicon-cog'},
				{caption: 'Audit', state: 'app.audit', icon: 'glyphicon-check'}
			];
		}

		getUserList() : IPromise<app.models.UserList> {
			return this.httpGet('/api/admin/getUsers');
		}
	}

	angular
		.module('app.core')
		.service('AdminService', AdminService);
}