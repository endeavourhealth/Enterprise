/// <reference path="../lib/angular/angular.d.ts" />
module Dashboard
{
    export interface IDashboardService {
        getEngineHistory();
        getRecentDocumentsData();
        getEngineState();
        getReportActivityData();
    }

    class DashboardService implements IDashboardService
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
            vm.http.get("services/dashboard.enginehistory.json")
                .then(function(response) {
                    vm.rootScope.$broadcast("enginehistory.updated", {data: response.data});
                });
        }
        getRecentDocumentsData() {
            var vm = this;
            vm.http.get("services/dashboard.recentdocuments.json")
                .then(function(response) {
                    vm.rootScope.$broadcast("recentdocuments.updated", {data: response.data});
                });
        }
        getEngineState() {
            var vm = this;
            vm.http.get("services/dashboard.enginestate.json")
                .then(function(response) {
                    vm.rootScope.$broadcast("enginestate.updated", {data: response.data});
                });
        }
        getReportActivityData() {
            var vm = this;
            vm.http.get("services/dashboard.reportactivity.json")
                .then(function(response) {
                    vm.rootScope.$broadcast("reportactivity.updated", {data: response.data});
                });        }
    }

    angular
        .module("Dashboard")
        .service("DashboardService", DashboardService);
}