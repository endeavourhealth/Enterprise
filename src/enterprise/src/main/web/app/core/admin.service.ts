/// <reference path="../../typings/tsd.d.ts" />

module app.core {
	'use strict';
	
	export interface IAdminService {
		getCurrentUser() : ng.IPromise<any>;
	}

	export class AdminService implements IAdminService {
		static $inject = ["$http", "$q"];

		constructor(private http:ng.IHttpService, private promise:ng.IQService) {
		}

		getCurrentUser():ng.IPromise<any> {
			var defer = this.promise.defer();
			this.http.get("/api/user")
				.then(function (response) {
					defer.resolve(response.data);
				})
				.catch(function (exception) {
					defer.reject(exception);
				});

			return defer.promise;
		}
	}

	angular
		.module("app.core")
		.service("AdminService", AdminService);
}