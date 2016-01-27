/// <reference path="UserInRole.ts" />

module app.models {
	'use strict';

	export class User {
		userUuid:string;
		title:string;
		forename:string;
		surname:string;
		email:string;
		userInRoles:UserInRole[];
		initialUserInRoleUuid:string;
	}
}