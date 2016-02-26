/// <reference path="../../typings/tsd.d.ts" />

module app.layout {
	import IRootScopeService = angular.IRootScopeService;
	import IStateService = angular.ui.IStateService;
	import IModalService = angular.ui.bootstrap.IModalService;
	import IModalServiceInstance = angular.ui.bootstrap.IModalServiceInstance;
	class ShellController {
		warning : IModalServiceInstance;
		timedout : IModalServiceInstance;

		static $inject = ['$scope', '$uibModal', '$state'];

		constructor($scope : IRootScopeService, $modal : IModalService, $state : IStateService) {
			var vm = this;

			function closeModals() {
				if (vm.warning) {
					vm.warning.close();
					vm.warning = null;
				}

				if (vm.timedout) {
					vm.timedout.close();
					vm.timedout = null;
				}
			}

			$scope.$on('IdleStart', function () {
				closeModals();

				vm.warning = $modal.open({
					templateUrl: 'warning-dialog.html',
					windowClass: 'modal-danger'
				});
			});

			$scope.$on('IdleEnd', function () {
				closeModals();
			});

			$scope.$on('IdleTimeout', function () {
				closeModals();
				$state.transitionTo('login');
				vm.timedout = $modal.open({
					templateUrl: 'timedout-dialog.html',
					windowClass: 'modal-danger'
				});
			});
		}
	}

	angular.module('app.layout')
		.controller('ShellController', ShellController);
}
