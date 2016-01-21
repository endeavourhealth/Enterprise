/// <reference path="../../typings/tsd.d.ts" />

module app.core
{
    export interface IAdminService {
    }

    export class AdminService implements IAdminService
    {
        static $inject = ["$http", "$rootScope"];
        private rootScope : ng.IRootScopeService;
        private http : ng.IHttpService;

        constructor(protected $http: ng.IHttpService, protected $rootScope: ng.IRootScopeService) {
            this.rootScope = $rootScope;
            this.http = $http;
        }
    }

    angular
        .module("app.core")
        .service("AdminService", AdminService);
}