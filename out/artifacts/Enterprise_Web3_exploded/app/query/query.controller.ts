/// <reference path="../../typings/tsd.d.ts" />

var flowchart : any;

module app.query {
	'use strict';

	class QueryController {
	}

	angular
		.module('app.query')
		//
		// Simple service to create a prompt.
		//
		.factory('prompt', function () {
			// Return the browsers prompt function.
			return prompt;
		})
		.factory("criteriaService", function () {
			var node = {verb: "Node Verb", label: "Node Label", nextStep: "Then"};
			function getNodeVerb() {
				return node.verb;
			}
			function setNodeVerb(value : any) {
				node.verb = value;
			}
			function getNodeLabel() {
				return node.label;
			}
			function setNodeLabel(value : any) {
				node.label = value;
			}
			function getNodeNextStep() {
				return node.nextStep;
			}
			function setNodeNextStep(value : any) {
				node.nextStep = value;
			}
			return {
				getNodeVerb: getNodeVerb,
				setNodeVerb: setNodeVerb,
				getNodeLabel: getNodeLabel,
				setNodeLabel: setNodeLabel,
				getNodeNextStep: getNodeNextStep,
				setNodeNextStep: setNodeNextStep,
			}
		})
		.controller('QueryController', ['$scope', 'prompt', 'criteriaService', function QueryController ($scope : any, prompt : any, criteriaService : any) {
			$scope.$watch(function () {
				return criteriaService.getNodeVerb();
			}, function (value : any) {
				$scope.nodeVerb = value;
			});
			$scope.$watch(function () {
				return criteriaService.getNodeLabel();
			}, function (value : any) {
				$scope.nodeLabel = value;
			});
			$scope.$watch(function () {
				return criteriaService.getNodeNextStep();
			}, function (value : any) {
				$scope.nodeNextStep = value;
			});

			$scope.nodeLabelChange = function () {
				var selectedNodes = $scope.chartViewModel.getSelectedNodes();
				for (var i = 0; i < selectedNodes.length; ++i) {
					var node = selectedNodes[i];
					node.data.name = $scope.nodeLabel;
				};
			};

			$scope.nodeVerbChange = function () {
				var selectedNodes = $scope.chartViewModel.getSelectedNodes();
				for (var i = 0; i < selectedNodes.length; ++i) {
					var node = selectedNodes[i];
					node.data.verb[0].name = $scope.nodeVerb;
				};
			};

			$scope.nodeNextStepChange = function () {
				var selectedNodes = $scope.chartViewModel.getSelectedNodes();
				for (var i = 0; i < selectedNodes.length; ++i) {
					var node = selectedNodes[i];
					node.data.nextStep[0].name = $scope.nodeNextStep;
				};
			};


			$scope.verbs = ['START QUERY','END QUERY','Narrow down to patients with','Remove patients with','Count patients remaining','Count patients who are','Calculate'];
			$scope.nextSteps = ['THEN','MERGE','END QUERY'];
			$scope.groups = ['Allergy','Condition','Procedure','Medication','Immunisation','Observation','Patient','Encounter','Appointment'];
			$scope.statuses = ['Current','Past'];
			//
			// Selects the next node id.
			//
			var nextNodeID = 10;

			//
			// Setup the data-model for the chart.
			//
			var chartDataModel = {
				"nodes": [
					{
						"name": "PATIENTS: Age 30-74",
						"id": 10,
						"x": 21,
						"y": 6,
						"verb": [
							{
								"name": "START QUERY"
							}
						],
						"nextStep": [
							{
								"name": "THEN"
							}
						]
					},
					{
						"name": "HYPERTENSION",
						"id": 11,
						"x": 329,
						"y": 6,
						"verb": [
							{
								"name": "Narrow down to patients with"
							}
						],
						"nextStep": [
							{
								"name": "THEN"
							}
						]
					},
					{
						"name": "Pre-existing CVD/ STROKE/Diabetes",
						"id": 12,
						"x": 628.9999923706055,
						"y": 6,
						"verb": [
							{
								"name": "Remove patients with"
							}
						],
						"nextStep": [
							{
								"name": "THEN"
							}
						]
					},
					{
						"name": " CVD RISK SCORE >= 20%",
						"id": 13,
						"x": 41.99999237060547,
						"y": 201,
						"verb": [
							{
								"name": "Narrow down to patients with"
							}
						],
						"nextStep": [
							{
								"name": "THEN"
							},
							{
								"name": "ALSO"
							}
						]
					},
					{
						"name": "TOTAL NO. OF PATIENTS",
						"id": 14,
						"x": 342,
						"y": 165,
						"verb": [
							{
								"name": "Count patients remaining"
							}
						],
						"nextStep": [
							{
								"name": "THEN"
							}
						]
					},
					{
						"name": "CURRENTLY ON STATINS",
						"id": 15,
						"x": 341,
						"y": 295,
						"verb": [
							{
								"name": "Count patients who are"
							}
						],
						"nextStep": [
							{
								"name": "THEN"
							}
						]
					},
					{
						"name": " % ON STATINS",
						"id": 16,
						"x": 651,
						"y": 234,
						"verb": [
							{
								"name": "Calculate"
							}
						],
						"nextStep": [
							{
								"name": "END QUERY"
							}
						]
					}
				],
				"connections": [
					{
						"source": {
							"nodeID": 10,
							"connectorIndex": 0
						},
						"dest": {
							"nodeID": 11,
							"connectorIndex": 0
						}
					},
					{
						"source": {
							"nodeID": 11,
							"connectorIndex": 0
						},
						"dest": {
							"nodeID": 12,
							"connectorIndex": 0
						}
					},
					{
						"source": {
							"nodeID": 13,
							"connectorIndex": 0
						},
						"dest": {
							"nodeID": 14,
							"connectorIndex": 0
						}
					},
					{
						"source": {
							"nodeID": 13,
							"connectorIndex": 1
						},
						"dest": {
							"nodeID": 15,
							"connectorIndex": 0
						}
					},
					{
						"source": {
							"nodeID": 14,
							"connectorIndex": 0
						},
						"dest": {
							"nodeID": 16,
							"connectorIndex": 0
						}
					},
					{
						"source": {
							"nodeID": 15,
							"connectorIndex": 0
						},
						"dest": {
							"nodeID": 16,
							"connectorIndex": 0
						}
					},
					{
						"source": {
							"nodeID": 12,
							"connectorIndex": 0
						},
						"dest": {
							"nodeID": 13,
							"connectorIndex": 0
						}
					}
				]
			};


			//
			// Add a new node to the chart.
			//
			$scope.addNewNode = function () {
				//
				// Template for a new node.
				//
				var newNodeDataModel = {
					name: "Node Label",
					id: nextNodeID++,
					x: 50,
					y: 50,
					verb: [
						{
							name: "Narrow down to patients with"
						}
					],
					nextStep: [
						{
							name: "Then"
						}
					],
				};

				$scope.chartViewModel.addNode(newNodeDataModel);
			};

			//
			// Add an input connector to selected nodes.
			//
			$scope.addNewInputConnector = function () {
				var connectorName = prompt("Enter a connector name:", "New connector");
				if (!connectorName) {
					return;
				}

				var selectedNodes = $scope.chartViewModel.getSelectedNodes();
				for (var i = 0; i < selectedNodes.length; ++i) {
					var node = selectedNodes[i];
					node.addInputConnector({
						name: connectorName,
					});
				}
			};

			//
			// Add an output connector to selected nodes.
			//
			$scope.addNewOutputConnector = function () {
				var connectorName = prompt("Enter a connector name:", "New connector");
				if (!connectorName) {
					return;
				}

				var selectedNodes = $scope.chartViewModel.getSelectedNodes();
				for (var i = 0; i < selectedNodes.length; ++i) {
					var node = selectedNodes[i];
					node.addOutputConnector({
						name: connectorName,
					});
				}
			};

			//
			// Delete selected nodes and connections.
			//
			$scope.deleteSelected = function () {
				$scope.chartViewModel.deleteSelected();
			};


			$scope.ShowDataModel = function () {
				$scope.dataModel = $scope.showDataModel;
			};


			//
			// Create the view-model for the chart and attach to the scope.
			//
			$scope.chartViewModel = new flowchart.ChartViewModel(chartDataModel);
		}])

}





