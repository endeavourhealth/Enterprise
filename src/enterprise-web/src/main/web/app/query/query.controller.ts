/// <reference path="../../typings/tsd.d.ts" />

var flowchart : any;

module app.query {
	import TestEditorController = app.dialogs.TestEditorController;
	import IModalService = angular.ui.bootstrap.IModalService;
	import IModalSettings = angular.ui.bootstrap.IModalSettings;
	import Test = app.models.Test;
	import ILibraryService = app.core.ILibraryService;
	import LibraryItem = app.models.LibraryItem;

	'use strict';

	class QueryController {
	}

	angular
		.module('app.query')
		.directive('clearQuery', function () {
			return {
				template: '<div>' +
				'<div class="modal-dialog">' +
				'<div class="modal-content">' +
				'<div class="modal-header">' +
				'<button type="button" class="close" ng-click="toggleClearQuery()" aria-hidden="true">&times;</button>' +
				'<h4 class="modal-title">{{ title }}</h4>' +
				'</div>' +
				'<div class="modal-body" ng-transclude></div>' +
				'</div>' +
				'</div>' +
				'</div>',
				restrict: 'E',
				transclude: true,
				replace:true,
				scope:true,
				link: function postLink(scope: any, element: any, attrs: any) {
					scope.title = attrs.title;
				}
			};
		})
		.controller('QueryController', ['$scope', '$stateParams', '$uibModal','$window','LibraryService','AdminService',
			function QueryController ($scope : any, $stateParams : any, $modal : IModalService, $window : any,
									  libraryService : ILibraryService, adminService : IAdminService) {

				var libraryItem : LibraryItem;

				var itemAction = $stateParams.itemAction;
				var itemUuid = $stateParams.itemUuid;

				$scope.queryFolderUuid = null;
				$scope.queryFolderName = "";
				$scope.queryUuid = null;
				$scope.queryName = "";
				$scope.queryDescription = "";

				switch(itemAction) {
					case "add":
						$scope.queryFolderUuid = itemUuid;
						break;
					case "edit":
						$scope.queryUuid = itemUuid;
						break;
					case "view":
						$scope.queryUuid = itemUuid;
						break;
					default:
				}

				//
				// Setup the data-model for the chart.
				//
				var document = {
					queryDocument: {
						folder: {
							uuid: $scope.queryFolderUuid,
							name: $scope.queryFolderName
						},
						libraryItem: {
							uuid: $scope.queryUuid,
							name: $scope.queryName,
							description: $scope.queryDescription,
							folderUuid: $scope.queryFolderUuid,
							query: {
								startingRules: {
									ruleId : <any>[]
								},
								rule: <any>[]
							}
						}
					}
				};

				$scope.queryNameChange = function () {
					$scope.chartViewModel.data.queryDocument.libraryItem.name = $scope.queryName;
				};

				$scope.queryDescriptionChange = function () {
					$scope.chartViewModel.data.queryDocument.libraryItem.description = $scope.queryDescription;
				};

				$scope.ruleDescriptionChange = function () {
					var selectedRule = $scope.chartViewModel.getSelectedRule();
					selectedRule.data.description = $scope.ruleDescription;
				};

				$scope.rulePassActionChange = function () {
					var selectedRule = $scope.chartViewModel.getSelectedRule();
					selectedRule.data.onPass.action = $scope.rulePassAction;
					if ($scope.rulePassAction!="GOTO_RULES") {
						selectedRule.data.onPass.ruleId = <any>[];
					}
				};

				$scope.ruleFailActionChange = function () {
					var selectedRule = $scope.chartViewModel.getSelectedRule();
					selectedRule.data.onFail.action = $scope.ruleFailAction;
					if ($scope.ruleFailAction!="GOTO_RULES") {
						selectedRule.data.onFail.ruleId = <any>[];
					}
				};

				$scope.nextRuleID = 0;

				$scope.results = ['','GOTO_RULES','INCLUDE','EXCLUDE'];

				$scope.$on('editTest', function(event : any, ruleId : any) {
					if (ruleId!="0") {
						$scope.ruleId = ruleId;

						var selectedRule = $scope.chartViewModel.getSelectedRule();

						var test : Test = selectedRule.data.test;

						TestEditorController.open($modal, test)
							.result.then(function(resultData : Test){

							selectedRule.data.test = resultData;
						});
					}
				});

				$scope.$on('ruleDescription', function(event : any, description : any) {
					$scope.ruleDescription = description;
				});

				$scope.$on('rulePassAction', function(event : any, action : any) {
					$scope.rulePassAction = action;
				});

				$scope.$on('ruleFailAction', function(event : any, action : any) {
					$scope.ruleFailAction = action;
				});

				$scope.dataModel = false;
				$scope.ShowDataModel = function () {
					$scope.dataModel = !$scope.dataModel;
				};

				$scope.showClearQuery = false;
				$scope.toggleClearQuery = function () {
					$scope.showClearQuery = !$scope.showClearQuery;
				};

				$scope.clearQueryYes = function () {
					$scope.chartViewModel.clearQuery();
					$scope.nextRuleID = 0;
					$scope.ruleDescription = "";
					$scope.rulePassAction = "";
					$scope.ruleFailAction = "";
					this.toggleClearQuery();
				};

				//
				// Add a new rule to the chart.
				//
				$scope.addNewRule = function () {
					//
					// Template for a new rule.
					//

					if ($scope.nextRuleID==0) {

						var newStartRuleDataModel = {
							description: "START",
							id: $scope.nextRuleID++,
							layout: {
								x: -162,
								y: 25
							},
							onPass: {
								action: "",
								ruleId : <any>[]
							},
							onFail: {
								action: "",
								ruleId: <any>[]
							}
						};

						$scope.chartViewModel.addRule(newStartRuleDataModel);

						var newRuleDataModel = {
							description: "Rule Description",
							id: $scope.nextRuleID++,
							layout: {
								x: 194,
								y: 5
							},
							onPass: {
								action: "",
								ruleId: <any>[]
							},
							onFail: {
								action: "",
								ruleId: <any>[]
							}
						};

						$scope.chartViewModel.addRule(newRuleDataModel);

						$scope.chartViewModel.addStartingRule(1);
					}
					else {
						var newRuleDataModel = {
							description: "Rule Description",
							id: $scope.nextRuleID++,
							layout: {
								x: 100,
								y: 10
							},
							onPass: {
								action: "",
								ruleId: <any>[]
							},
							onFail: {
								action: "",
								ruleId: <any>[]
							}
						};

						$scope.chartViewModel.addRule(newRuleDataModel);
					}
				};

				//
				// Delete selected rule and connections.
				//
				$scope.deleteSelected = function () {
					$scope.chartViewModel.deleteSelected();
				};

				$scope.save = function () {
					libraryItem = $scope.chartViewModel.data.queryDocument.libraryItem;

					libraryService.saveLibraryItem(libraryItem)
						.then(function(libraryItem : LibraryItem) {
							libraryItem.uuid = libraryItem.uuid;
							alert(libraryItem.folderUuid+" : "+ libraryItem.uuid);
							adminService.clearPendingChanges();
						});
				}

				$scope.close = function () {
					// put code in here to check for changes
					$window.history.back();
				}

				$scope.saveAndClose = function () {
					// save code here
					$window.history.back();
				}

				//
				// Create the view-model for the chart and attach to the scope.
				//
				$scope.chartViewModel = new flowchart.ChartViewModel(document);

				$scope.queryName = $scope.chartViewModel.data.queryDocument.libraryItem.name;
				$scope.queryDescription = $scope.chartViewModel.data.queryDocument.libraryItem.description;


			}])

}
