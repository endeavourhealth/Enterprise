/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../blocks/logger.service.ts" />

module app.admin {
	import User = app.models.User;
	'use strict';

	class AdminController {
		userType : string;
		userList : app.models.User[];

		static $inject = ['LoggerService', 'AdminService'];

		constructor(private logger:app.blocks.ILoggerService, private adminService : app.core.IAdminService) {
			this.userType = 'all';
			this.loadUsers();
		}

		editUser(user:User) {
			this.logger.success('Edit ' + user.username);
		}

		viewUser(user:User) {
			this.logger.info('View ' + user.username);
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
