/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../blocks/logger.service.ts" />

module app.admin {
	'use strict';

	class AdminController {
		userType : string;
		userList : app.models.User[];

		static $inject = ['LoggerService', 'AdminService'];

		constructor(private logger:app.blocks.ILoggerService, private adminService : app.core.IAdminService) {
			logger.success('Admin constructed', 'AdminData', 'Administration');
			this.userType = 'all';
			this.loadUsers();
		}

		private loadUsers() {
			var vm = this;
			vm.adminService.getUserList()
				.then(function(result) {
						vm.userList = result;
				});
		}
	}

	angular
		.module('app.admin')
		.controller('AdminController', AdminController);
}
