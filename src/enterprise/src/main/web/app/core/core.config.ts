/// <reference path="../../typings/tsd.d.ts" />

module app.core {
	import IIdleProvider = angular.idle.IIdleProvider;
	import IKeepAliveProvider = angular.idle.IKeepAliveProvider;
	'use strict';

	class Config {

		static $inject = ['IdleProvider', 'KeepaliveProvider'];

		constructor(IdleProvider:IIdleProvider, KeepaliveProvider:IKeepAliveProvider) {
			toastr.options.timeOut = 4000;
			toastr.options.positionClass = 'toast-bottom-right';
			IdleProvider.idle(300);
			IdleProvider.timeout(10);
			KeepaliveProvider.interval(10);
		}
	}

	angular
		.module('app.core')
		.config(Config);
}