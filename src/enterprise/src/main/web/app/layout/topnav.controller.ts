/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/admin.service.ts" />

module app.layout {
    class TopnavController {
        rootScope : ng.IRootScopeService;
        currentUser;

        static $inject = ["$rootScope", "AdminService"];
        constructor(
            private $rootScope : ng.IRootScopeService,
            private adminService: app.core.IAdminService) {
            this.rootScope = $rootScope;
            TopnavController.setupEventListeners(this);

            adminService.getCurrentUser();
        }

        private static setupEventListeners(instance : TopnavController) {
            instance.rootScope.$on("currentuser.updated", function(event, message) {
                instance.currentUser = message.data;
            });
        }
    }

    angular.module('app.layout')
        .controller('TopnavController', TopnavController);
}