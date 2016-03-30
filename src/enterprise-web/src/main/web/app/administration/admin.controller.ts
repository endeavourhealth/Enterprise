/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../blocks/logger.service.ts" />

module app.admin {
	import User = app.models.User;
	import UserEditorController = app.dialogs.UserEditorController;
	'use strict';

	class AdminController {
		userType : string;
		userList : app.models.User[];

		static $inject = ['LoggerService', 'AdminService', '$uibModal'];

		constructor(private logger:app.blocks.ILoggerService,
								private adminService : app.core.IAdminService,
								private $modal : IModalService) {
			this.userType = 'all';
			this.loadUsers();
		}

		editUser(user:User) {
			var vm = this;
			UserEditorController.open(vm.$modal, user, false)
				.result.then(function(result) {
					// vm.adminService.saveUser(result);
			});
		}

		viewUser(user:User) {
			var vm = this;
			UserEditorController.open(vm.$modal, user, true);
		}

		deleteUser(user:User) {
			this.logger.error('Delete ' + user.username);
		}

		private loadUsers() {
			var vm = this;
			vm.adminService.getUserList()
				.then(function(result) {
						vm.userList = result.users;
				});
		}
	}

	angular
		.module('app.admin')
		.controller('AdminController', AdminController);
}
