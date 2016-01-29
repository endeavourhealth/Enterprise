/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../blocks/logger.service.ts" />

module app.login {
	'use strict';

	class LoginController {

		static $inject = ['LoggerService'];

		constructor(private logger:app.blocks.ILoggerService) {
			logger.success('Login constructed', 'LoginData', 'Login');
		}
	}

	angular
		.module('app.login')
		.controller('LoginController', LoginController);
}
