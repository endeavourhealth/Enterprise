
//
// Global accessor.
//
var flowchart = {};

// Module.
(function () {

	flowchart.ruleWidth = 250;
	flowchart.ruleDescriptionHeight = 60;
	flowchart.connectorHeight = 55;

	//
	// Compute the Y coordinate of a connector, given its index.
	//
	flowchart.computeConnectorY = function (connectorIndex) {
		return flowchart.ruleDescriptionHeight + (connectorIndex * flowchart.connectorHeight);
	}

	//
	// Compute the position of a connector in the graph.
	//
	flowchart.computeConnectorPos = function (rule, connectorIndex, inputConnector) {
		return {
			x: rule.x() + (inputConnector ? 0 : flowchart.ruleWidth),
			y: rule.y() + flowchart.computeConnectorY(connectorIndex),
		};
	};

	//
	// View model for a queryDocument.
	//
	flowchart.QueryDocumentViewModel = function (queryDocumentDataModel) {

		this.data = queryDocumentDataModel;

		this.folder = function () {
			return this.data.folder;
		}

		this.libraryItem = function () {
			return this.data.libraryItem;
		}

		this.report = function () {
			return this.data.report;
		}
	};

	//
	// View model for a folder.
	//
	flowchart.FolderViewModel = function (folderDataModel) {

		this.data = folderDataModel;

		this.uuid = function () {
			return this.data.uuid;
		}

		this.name = function () {
			return this.data.name;
		}

		this.parentUuid = function () {
			return this.data.parentUuid;
		}
	};

	//
	// View model for a libraryItem.
	//
	flowchart.LibraryItemViewModel = function (libraryItemDataModel) {

		this.data = libraryItemDataModel;

		this.uuid = function () {
			return this.data.uuid;
		}

		this.name = function () {
			return this.data.name;
		}

		this.description = function () {
			return this.data.description;
		}

		this.folderUuid = function () {
			return this.data.folderUuid;
		}

		this.query = function () {
			return this.data.query;
		}
	};

	//
	// View model for a query.
	//
	flowchart.QueryViewModel = function (queryDataModel) {

		this.data = queryDataModel;

		this.parentQueryUuid = function () {
			return this.data.parentQueryUuid;
		}

		this.startingRules = function () {
			return this.data.startingRules;
		}

		this.rule = function () {
			return this.data.rule;
		}

	};

	//
	// View model for a startingRule.
	//
	flowchart.StartingRulesViewModel = function (startingRulesDataModel) {

		this.data = startingRulesDataModel;

		this.ruleId = function () {
			return this.data;
		}

	};

	//
	// View model for a rule.
	//
	flowchart.RuleViewModel = function (ruleDataModel) {

		this.data = ruleDataModel;

		this._selected = false;

		this.description = function () {
			return this.data.description || "";
		};

		this.id = function () {
			return this.data.id;
		};

		this.test = function () {
			return this.data.test;
		}

		this.testLibraryItemUUID = function () {
			return this.data.testLibraryItemUUID;
		}

		this.queryLibraryItemUUID = function () {
			return this.data.queryLibraryItemUUID;
		}

		this.expression = function () {
			return this.data.expression;
		}

		this.onPassAction = function () {
			return this.data.onPass.action;
		}

		this.onPassRuleId = function () {
			return this.data.onPass.ruleId;
		}

		this.onFailAction = function () {
			return this.data.onFail.action;
		}

		this.onFailRuleId = function () {
			return this.data.onFail.ruleId;
		}

		this.x = function () {
			return this.data.layout.x;
		};

		this.y = function () {
			return this.data.layout.y;
		};

		this.width = function () {
			return flowchart.ruleWidth;
		}

		this.height = function () {
			var numConnectors = 2;
			return flowchart.computeConnectorY(numConnectors);
		}

		this.select = function () {
			this._selected = true;
		};

		this.deselect = function () {
			this._selected = false;
		};

		this.toggleSelected = function () {
			this._selected = !this._selected;
		};

		this.selected = function () {
			return this._selected;
		};

	};

	var createQueryDocumentViewModel = function (queryDocumentDataModel) {
		var queryDocumentViewModel = [];

		if (queryDocumentDataModel) {
			for (var i = 0; i < queryDocumentDataModel.length; ++i) {
				queryDocumentViewModel.push(new flowchart.QueryDocumentViewModel(queryDocumentDataModel[i]));
			}
		}

		return queryDocumentViewModel;
	};

	var createFolderViewModel = function (folderDataModel) {
		var folderViewModel = [];

		if (folderDataModel) {
			for (var i = 0; i < folderDataModel.length; ++i) {
				folderViewModel.push(new flowchart.FolderViewModel(folderDataModel[i]));
			}
		}

		return folderViewModel;
	};

	var createLibraryItemViewModel = function (libraryItemDataModel) {
		var libraryItemViewModel = [];

		if (libraryItemDataModel) {
			for (var i = 0; i < libraryItemDataModel.length; ++i) {
				libraryItemViewModel.push(new flowchart.LibraryItemViewModel(libraryItemDataModel[i]));
			}
		}

		return libraryItemViewModel;
	};

	var createQueryViewModel = function (queryDataModel) {
		var queryViewModel = [];

		if (queryDataModel) {
			for (var i = 0; i < queryDataModel.length; ++i) {
				queryViewModel.push(new flowchart.QueryViewModel(queryDataModel[i]));
			}
		}

		return queryViewModel;
	};

	var createStartingRulesViewModel = function (startingRulesDataModel) {
		var startingRulesViewModel = [];

		if (startingRulesDataModel) {
			for (var i = 0; i < startingRulesDataModel.length; ++i) {
				startingRulesViewModel.push(new flowchart.StartingRulesViewModel(startingRulesDataModel[i]));
			}
		}

		return startingRulesViewModel;
	};

	var createRuleViewModel = function (ruleDataModel) {
		var ruleViewModel = [];

		if (ruleDataModel) {
			for (var i = 0; i < ruleDataModel.length; ++i) {
				ruleViewModel.push(new flowchart.RuleViewModel(ruleDataModel[i]));
			}
		}

		return ruleViewModel;
	};

	//
	// View model for the chart.
	//
	flowchart.ChartViewModel = function (document) {

		// reference to the query document data
		this.data = document;

		// create rules view model
		this.rule = createRuleViewModel(this.data.queryDocument.libraryItem.query.rule);

		// create startingRules view model
		this.startingRules = createStartingRulesViewModel(this.data.queryDocument.libraryItem.query.startingRules.ruleId);

		this.libraryItem = createLibraryItemViewModel(this.data.queryDocument.libraryItem);

		//
		// Create a view model for a new connection.
		//
		this.createNewConnection = function (sourceRule, sourceRuleId, destRuleId, connectorIndex) {

			if (sourceRule.description()=="START") {
				this.addStartingRule(destRuleId);
			}
			else {
				if (connectorIndex==0) {
					sourceRule.data.onPass.action = "Next Rule";
					if (!sourceRule.data.onPass.ruleId) {
						sourceRule.data.onPass.ruleId = [];
					}
					sourceRule.data.onPass.ruleId.push(destRuleId);
				}
				else if (connectorIndex==1) {
					sourceRule.data.onFail.action = "Next Rule";
					if (!sourceRule.data.onFail.ruleId) {
						sourceRule.data.onFail.ruleId = [];
					}
					sourceRule.data.onFail.ruleId.push(destRuleId);
				}
			}
		};

		this.addStartingRule = function (destRuleId) {
			if (!this.data.queryDocument.libraryItem.query.startingRules.ruleId) {
				this.data.queryDocument.libraryItem.query.startingRules.ruleId = [];
			}
			this.data.queryDocument.libraryItem.query.startingRules.ruleId.push(destRuleId);
			//
			// Update the startingRules view model.
			//
			this.startingRules.push(new flowchart.StartingRulesViewModel(destRuleId));
		}

		//
		// Add a rule to the view model.
		//
		this.addRule = function (ruleDataModel) {
			if (!this.data.queryDocument.libraryItem.query.rule) {
				this.data.queryDocument.libraryItem.query.rule = [];
			}

			//
			// Update the query document data model.
			//
			this.data.queryDocument.libraryItem.query.rule.push(ruleDataModel);

			//
			// Update the rule view model.
			//
			this.rule.push(new flowchart.RuleViewModel(ruleDataModel));
		}

		this.clearQuery = function () {
			this.data.queryDocument.libraryItem.query.rule.length = 0;
			this.data.queryDocument.libraryItem.query.startingRules.ruleId.length = 0;
			this.rule.length = 0;
			this.startingRules.length = 0;

		}

		//
		// Deselect all rules in the chart.
		//
		this.deselectAll = function () {
			var rule = this.rule;
			for (var i = 0; i < rule.length; ++i) {
				var r = rule[i];
				r.deselect();
			}
		};

		//
		// Update the location of the rule and its connectors.
		//
		this.updateSelectedRuleLocation = function (deltaX, deltaY) {

			var selectedRule = this.getSelectedRule();

			selectedRule.data.layout.x += deltaX;
			selectedRule.data.layout.y += deltaY;
		};

		//
		// Handle mouse click on a particular rule.
		//
		this.handleRuleClicked = function (rule, ctrlKey) {

			if (ctrlKey) {
				rule.toggleSelected();
			}
			else {
				this.deselectAll();
				rule.select();
			}

			// Move rule to the end of the list so it is rendered after all the other.
			// This is the way Z-order is done in SVG.

			var ruleIndex = this.rule.indexOf(rule);
			if (ruleIndex == -1) {
				throw new Error("Failed to find rule in view model!");
			}
			this.rule.splice(ruleIndex, 1);
			this.rule.push(rule);
		};

		//
		// Delete all rules that are selected.
		//
		this.deleteSelected = function () {

			var newRuleViewModels = [];
			var newRuleDataModels = [];

			var deletedRuleIds = [];

			//
			// Sort rule into:
			//		rule to keep and
			//		rule to delete.
			//

			var selectedRule = this.getSelectedRule();
			var selectedRuleId = selectedRule.data.id;

			for (var ruleIndex = 0; ruleIndex < this.rule.length; ++ruleIndex) {
				var newNextRuleViewModels = [];

				var r = this.rule[ruleIndex];

				for (var nextRulesIndex = 0; nextRulesIndex < r.data.onPass.ruleId.length; ++nextRulesIndex) {
					var ruleId = r.data.onPass.ruleId[nextRulesIndex];
					if (selectedRuleId!=ruleId) {
						// Only retain non-selected next rules.
						newNextRuleViewModels.push(ruleId);
					}
				}

				r.data.onPass.ruleId.length = 0;
				r.data.onPass.ruleId.push.apply(r.data.onPass.ruleId, newNextRuleViewModels);
				if (r.data.onPass.ruleId.length==0) {
					r.data.onPass.action = "";
				}
			}

			for (var ruleIndex = 0; ruleIndex < this.rule.length; ++ruleIndex) {
				var newNextRuleViewModels = [];

				var r = this.rule[ruleIndex];

				for (var nextRulesIndex = 0; nextRulesIndex < r.data.onFail.ruleId.length; ++nextRulesIndex) {
					var ruleId = r.data.onFail.ruleId[nextRulesIndex];
					if (selectedRuleId!=ruleId) {
						// Only retain non-selected next rules.
						newNextRuleViewModels.push(ruleId);
					}
				}

				r.data.onFail.ruleId.length = 0;
				r.data.onFail.ruleId.push.apply(r.data.onFail.ruleId, newNextRuleViewModels);
				if (r.data.onFail.ruleId.length==0) {
					r.data.onFail.action = "";
				}
			}

			var newStartingRuleViewModels = [];
			var newStartingRuleDataModels = [];

			for (var startingRulesIndex = 0; startingRulesIndex < this.startingRules.length; ++startingRulesIndex) {
				var rule = this.startingRules[startingRulesIndex];
				var ruleId = rule.ruleId();

				if (selectedRuleId!=ruleId) {
					// Only retain non-selected starting rules.
					newStartingRuleViewModels.push(rule);
					newStartingRuleDataModels.push(rule.data);
				}
			}

			for (var ruleIndex = 0; ruleIndex < this.rule.length; ++ruleIndex) {
				var rule = this.rule[ruleIndex];
				if (!rule.selected()) {
					// Only retain non-selected rule.
					newRuleViewModels.push(rule);
					newRuleDataModels.push(rule.data);
				}
				else {
					deletedRuleIds.push(rule.data.id);
				}
			}

			//
			// Update rules
			//
			this.rule = newRuleViewModels;
			this.data.queryDocument.libraryItem.query.rule = newRuleDataModels;

			this.startingRules = newStartingRuleViewModels;
			this.data.queryDocument.libraryItem.query.startingRules.ruleId = newStartingRuleDataModels;
		};

		//
		// Get the array of rule that are currently selected.
		//
		this.getSelectedRule = function () {
			for (var i = 0; i < this.rule.length; ++i) {
				var rule = this.rule[i];
				if (rule.selected()) {
					break;
				}
			}

			return rule;
		};

	};

})();