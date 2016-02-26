/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/admin.service.ts" />
/// <reference path="../models/Role.ts" />
/// <reference path="../models/User.ts" />
/// <reference path="../models/UserInRole.ts" />

module app.layout {
	'use strict';

	class TopnavController {
		currentUser:app.models.User;
		selectedRole:app.models.UserInRole;

		static $inject = ['AdminService'];

		constructor(private adminService:app.core.IAdminService) {
			this.getCurrentUser();
		}

		getCurrentUser() {
			var vm:TopnavController = this;
			vm.currentUser = vm.adminService.getCurrentUser();
			//vm.updateRole(vm.currentUser.currentUserInRoleUuid);
		}

		updateRole(userInRoleUuid : string) {
			var vm = this;
			var matches = $.grep(vm.currentUser.userInRoles, function (e) {
				return e.userInRoleUuid === userInRoleUuid;
			});
			if (matches.length === 1) {
				vm.adminService.switchUserInRole(userInRoleUuid)
					.then(function(data) {
						vm.currentUser.currentUserInRoleUuid = userInRoleUuid;
						vm.selectedRole = matches[0];
					});
			}
		}
	}

	angular.module('app.layout')
		.controller('TopnavController', TopnavController);
}