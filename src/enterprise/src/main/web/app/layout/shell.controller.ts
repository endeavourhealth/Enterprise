/// <reference path="../../typings/tsd.d.ts" />

module app.layout {
	class ShellController {
		warning;
		timedout;

		static $inject = ['$scope', '$uibModal', '$state'];

		constructor($scope, $modal, $state) {
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
