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
		.controller('QueryController', ['$scope', '$stateParams', 'prompt', function QueryController ($scope : any, $stateParams : any, prompt : any) {
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
					"x": 34,
					"y": 121,
					"inputConnectors": [
						{
							"name": "Start Query"
						}
					],
					"outputConnectors": [
						{
							"name": "Then"
						}
					]
				},
				{
					"name": "HYPERTENSION",
					"id": 11,
					"x": 355,
					"y": 121,
					"inputConnectors": [
						{
							"name": "Narrow down to patients with"
						}
					],
					"outputConnectors": [
						{
							"name": "Then"
						}
					]
				},
				{
					"name": "Pre-existing CVD/ STROKE/Diabetes",
					"id": 12,
					"x": 668.9999923706055,
					"y": 121,
					"inputConnectors": [
						{
							"name": "Remove patients with"
						}
					],
					"outputConnectors": [
						{
							"name": "Then"
						}
					]
				},
				{
					"name": " CVD RISK SCORE >= 20%",
					"id": 13,
					"x": 1008.9999923706055,
					"y": 120,
					"inputConnectors": [
						{
							"name": "Narrow down to patients with"
						}
					],
					"outputConnectors": [
						{
							"name": "Then"
						},
						{
							"name": "Also"
						}
					]
				},
				{
					"name": "TOTAL NO. OF PATIENTS",
					"id": 14,
					"x": 1369,
					"y": 74,
					"inputConnectors": [
						{
							"name": "Count patients remaining"
						}
					],
					"outputConnectors": [
						{
							"name": "Then"
						}
					]
				},
				{
					"name": "CURRENTLY ON STATINS",
					"id": 15,
					"x": 1369,
					"y": 211,
					"inputConnectors": [
						{
							"name": "Count patients who are"
						}
					],
					"outputConnectors": [
						{
							"name": "Then"
						}
					]
				},
				{
					"name": " % ON STATINS",
					"id": 16,
					"x": 1740,
					"y": 138,
					"inputConnectors": [
						{
							"name": "Calculate"
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
						"nodeID": 12,
						"connectorIndex": 0
					},
					"dest": {
						"nodeID": 13,
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
				}
			]

			};

			// TODO: Parameter based routing actions
			// alert($stateParams.itemAction + ':' + $stateParams.itemUuid);



			//
			// Add a new node to the chart.
			//
			$scope.addNewNode = function () {

				var nodeName = prompt("Enter a node name:", "New node");
				if (!nodeName) {
					return;
				}

				//
				// Template for a new node.
				//
				var newNodeDataModel = {
					name: nodeName,
					id: nextNodeID++,
					x: 0,
					y: 0,
					inputConnectors: [
						{
							name: ""
						}
					],
					outputConnectors: [
						{
							name: "YES"
						},
						{
							name: "NO"
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

			$scope.ShowCriteria = function () {
				$scope.nodeCriteria = $scope.showCriteria;
			};

			//
			// Create the view-model for the chart and attach to the scope.
			//
			$scope.chartViewModel = new flowchart.ChartViewModel(chartDataModel);
		}])

}





