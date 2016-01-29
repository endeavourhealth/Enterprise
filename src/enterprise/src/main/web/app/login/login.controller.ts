/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../blocks/logger.service.ts" />

module app.login {
	import IIdleService = angular.idle.IIdleService;
	import IAdminService = app.core.IAdminService;
	import AdminService = app.core.AdminService;
	'use strict';

	export class LoginController {
		username:string;
		password:string;

		static $inject = ['LoggerService', 'Idle', '$state', 'AdminService'];

		constructor(private logger:app.blocks.ILoggerService,
								private Idle:IIdleService,
								private $state,
								private AdminService:IAdminService) {
			Idle.unwatch();
			AdminService.logout();
			logger.success('Login constructed', 'LoginData', 'Login');
		}

		login() {
			if (this.AdminService.login()) {
				this.logger.success('User logged in', this.username, 'Logged In');
				this.Idle.watch();
				this.$state.transitionTo('app.dashboard');
			}
		}

	}

	angular
		.module('app.login')
		.controller('LoginController', LoginController);
}
