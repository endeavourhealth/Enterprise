/// <reference path="../../typings/tsd.d.ts" />

module app.layout {
	class ShellController {
		static $inject = ['$scope', 'Idle', '$uibModal'];

		constructor($scope, Idle, $modal) {
			$scope.started = false;

			function closeModals() {
				if ($scope.warning) {
					$scope.warning.close();
					$scope.warning = null;
				}

				if ($scope.timedout) {
					$scope.timedout.close();
					$scope.timedout = null;
				}
			}

			$scope.$on('IdleStart', function () {
				closeModals();

				$scope.warning = $modal.open({
					templateUrl: 'warning-dialog.html',
					windowClass: 'modal-danger'
				});
			});

			$scope.$on('IdleEnd', function () {
				closeModals();
			});

			$scope.$on('IdleTimeout', function () {
				closeModals();
				$scope.timedout = $modal.open({
					templateUrl: 'timedout-dialog.html',
					windowClass: 'modal-danger'
				});
			});
		}
	}

	angular.module('app.layout')
		.controller('ShellController', ShellController);
}
