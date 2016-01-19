/// <reference path="../../lib/angular/angular.d.ts" />
/// <reference path="../../services/dashboard.service.ts" />

module Dashboard
{
    class DashboardController
    {
        static $inject = ["DashboardService"];

        _engineHistoryData: string;
        _recentDocumentsData: string;
        _engineState: string;
        _reportActivityData: string;

        constructor(private dashboardService: IDashboardService)
        {
            this._engineHistoryData = dashboardService.getEngineHistoryData();
            this._engineState = dashboardService.getEngineState();
            this._recentDocumentsData = dashboardService.getRecentDocumentsData();
            this._reportActivityData = dashboardService.getReportActivityData();
        }
    }

    angular
        .module('Dashboard')
        .controller('DashboardController',  DashboardController);
}
