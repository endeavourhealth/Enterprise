/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/admin.service.ts" />

module app.layout {
	class TopnavController {
		currentUser;

		static $inject = ["AdminService"];

		constructor(private adminService:app.core.IAdminService) {
			this.getCurrentUser();
		}

		getCurrentUser() {
			var vm = this;
			this.adminService.getCurrentUser()
				.then(function (data) {
					vm.currentUser = data;
				})
				.catch(function (data) {
					vm.currentUser = data;
				});
		}
	}

	angular.module('app.layout')
		.controller('TopnavController', TopnavController);
}