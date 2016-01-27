/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/admin.service.ts" />

module app.layout {
	'use strict';

	class TopnavController {
		currentUser;
		selectedRole;

		static $inject = ["AdminService"];

		constructor(private adminService:app.core.IAdminService) {
			this.getCurrentUser();
		}

		getCurrentUser() {
			var vm = this;
			this.adminService.getCurrentUser()
				.then(function (data) {
					vm.currentUser = data;
					var matches = $.grep(vm.currentUser.userInRoles, function (e) {
						return e.userInRoleUuid === vm.currentUser.initialUserInRoleUuid;
					});
					vm.selectedRole = matches[0];
				})
				.catch(function (data) {
					vm.currentUser = data;
				});
		}
	}

	angular.module('app.layout')
		.controller('TopnavController', TopnavController);
}