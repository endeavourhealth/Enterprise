/// <reference path="Role.ts" />

module app.models {
	'use strict';

	export class UserInRole {
		userInRoleUuid:string;
		organisationUuid:string;
		organisationName:string;
		endUserRole:Role;
	}
}