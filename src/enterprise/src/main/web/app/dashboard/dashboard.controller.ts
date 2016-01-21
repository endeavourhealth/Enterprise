/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />
/// <reference path="dashboard.model.ts"/>

module app.dashboard
{
    class DashboardController
    {
        rootScope : ng.IRootScopeService;
        _engineHistoryData: EngineHistoryItem[];
        _recentDocumentsData: RecentDocumentItem[];
        _engineState: EngineState;
        _reportActivityData: ReportActivityItem[];

        static $inject = ["$rootScope", "LibraryService"];
        constructor(
            private $rootScope : ng.IRootScopeService,
            private libraryService: app.core.ILibraryService) {
            this.rootScope = $rootScope;
            DashboardController.setupEventListeners(this);

            libraryService.getEngineHistory();
            libraryService.getRecentDocumentsData();
            libraryService.getEngineState();
            libraryService.getReportActivityData();
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
        .module('app.dashboard')
        .controller('DashboardController',  DashboardController);
}
