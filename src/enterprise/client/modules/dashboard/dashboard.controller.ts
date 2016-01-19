/// <reference path="../../lib/angular/angular.d.ts" />
/// <reference path="../../services/dashboard.service.ts" />

module Dashboard
{
    class DashboardController
    {
        static $inject = ["$rootScope", "DashboardService"];

        rootScope : ng.IRootScopeService;
        _engineHistoryData: string;
        _recentDocumentsData: string;
        _engineState: string;
        _reportActivityData: string;

        constructor(private $rootScope : ng.IRootScopeService, private dashboardService: IDashboardService) {
            this.rootScope = $rootScope;
            DashboardController.setupEventListeners(this);

            dashboardService.getEngineHistory();
            dashboardService.getRecentDocumentsData();
            dashboardService.getEngineState();
            dashboardService.getReportActivityData();
        }

        private static setupEventListeners(instance : DashboardController) {
            instance.rootScope.$on("enginehistory.updated", function(event, message) {
                instance._engineHistoryData = message.data;
            });

            instance.rootScope.$on("recentdocuments.updated", function(event, message) {
                instance._recentDocumentsData = message.data;
            });

            instance.rootScope.$on("enginestate.updated", function(event, message) {
                instance._engineState = message.data;
            });

            instance.rootScope.$on("reportactivity.updated", function(event, message) {
                instance._reportActivityData = message.data;
            });
        }
    }

    angular
        .module('Dashboard')
        .controller('DashboardController',  DashboardController);
}
