/// <reference path="../../typings/tsd.d.ts" />

module app.core
{
    export interface ILibraryService {
        getEngineHistory();
        getRecentDocumentsData();
        getEngineState();
        getReportActivityData();
    }

    export class LibraryService implements ILibraryService
    {
        static $inject = ["$http", "$rootScope"];
        private rootScope : ng.IRootScopeService;
        private http : ng.IHttpService;

        constructor(protected $http: ng.IHttpService, protected $rootScope: ng.IRootScopeService) {
            this.rootScope = $rootScope;
            this.http = $http;
        }

        getEngineHistory() {
            var vm = this;
            vm.http.get("app/core/data/enginehistory.json")
                .then(function(response) {
                    vm.rootScope.$broadcast("enginehistory.updated", {data: response.data});
                });
        }
        getRecentDocumentsData() {
            var vm = this;
            vm.http.get("app/core/data/recentdocuments.json")
                .then(function(response) {
                    vm.rootScope.$broadcast("recentdocuments.updated", {data: response.data});
                });
        }
        getEngineState() {
            var vm = this;
            vm.http.get("app/core/data/enginestate.json")
                .then(function(response) {
                    vm.rootScope.$broadcast("enginestate.updated", {data: response.data});
                });
        }
        getReportActivityData() {
            var vm = this;
            vm.http.get("app/core/data/reportactivity.json")
                .then(function(response) {
                    vm.rootScope.$broadcast("reportactivity.updated", {data: response.data});
                });
        }
    }

    angular
        .module("app.core")
        .service("LibraryService", LibraryService);
}