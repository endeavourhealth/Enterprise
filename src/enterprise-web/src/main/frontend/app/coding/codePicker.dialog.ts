import {Input, Component} from "@angular/core";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {ITreeOptions} from "angular2-tree-component";
import {CodeSetValue} from "../codeSet/models/CodeSetValue";
import {ExclusionTreeNode} from "./models/ExclusionTreeNode";
import {CodingService} from "./coding.service";
import {LoggerService} from "eds-common-js";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./codePicker.html')
})
export class CodePickerDialog {

	public static open(modalService: NgbModal,   selection : CodeSetValue[]) {
		const modalRef = modalService.open(CodePickerDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.resultData = selection;

		return modalRef;
	}

	@Input() resultData;
	options : ITreeOptions;

	highlightedMatch : CodeSetValue;
	previousSelection : CodeSetValue;
	highlightedSelection : CodeSetValue;

	searchData : string;
	searchResults : CodeSetValue[];
	parents : CodeSetValue[];
	children : CodeSetValue[];

	termCache : any;

	exclusionTreeData : ExclusionTreeNode[];

	constructor(private logger : LoggerService,
				protected activeModal : NgbActiveModal,
				private codingService : CodingService) {
		this.termCache = {};
		this.options = {
			childrenField : 'children',
			idField : 'code'
		};

		this.loadInitialCodeSet();
	}

	loadInitialCodeSet() {
		const SNOMED_ROOT_CONCEPT_ID : string = '138875005';

		let vm = this;
		vm.codingService.getCodeChildren(SNOMED_ROOT_CONCEPT_ID)
			.subscribe(
				(result) => {
					vm.searchResults = result;
					vm.parents = [];
					vm.children = [];
				});
	}

	search() {
		let vm = this;
		// vm.searchResults = vm.termlexSearch.getFindings(vm.searchData, vm.searchOptions);
		vm.codingService.searchCodes(vm.searchData)
			.subscribe(
				(result) => {
				vm.searchResults = result;
				vm.parents = [];
				vm.children = [];
			});
	}

	displayCode(itemToDisplay : CodeSetValue, replace : boolean) {
		let vm = this;

		if (vm.highlightedMatch) {
			vm.previousSelection = vm.highlightedMatch;
		}

		if (replace) {
			vm.searchResults = [itemToDisplay];
		}

		vm.codingService.getCodeChildren(itemToDisplay.code)
			.subscribe(
				(result) => vm.children = result

			);

		vm.codingService.getCodeParents(itemToDisplay.code)
			.subscribe(
				(result) => vm.parents = result
			);

		vm.highlightedMatch = itemToDisplay;


	}

	addToSelection(match : CodeSetValue) {

		for (var i = 0; i < this.resultData.length; ++i) {
			var baseType = this.resultData[i].baseType;

			if (baseType!=match.baseType) {
				this.logger.error('Each rule must have concepts of the same base type (i.e. Patient and Observation concepts cannot be mixed in the same rule)');
				return;

			}
		}

		let item : CodeSetValue = {
			code : match.code,
			term : match.term,
			dataType : match.dataType,
			parentType : match.parentType,
			baseType : match.baseType,
			present : match.present,
			valueFrom : match.valueFrom,
			valueTo : match.valueTo,
			units : match.units,
			includeChildren : true,
			exclusion : []
		};
		this.resultData.push(item);
	}

	removeFromSelection(item : CodeSetValue) {
		let i = this.resultData.indexOf(item);
		if (i !== -1) {
			this.resultData.splice(i, 1);
		}
	}

	displayExclusionTree(selection : CodeSetValue) {
		let vm = this;
		vm.highlightedSelection = selection;

		vm.codingService.getCodeChildren(selection.code)
			.subscribe(
				(result) => {

				let rootNode : ExclusionTreeNode = {
					codeSetValue : selection,
					children : []
				} as ExclusionTreeNode;

					result.forEach((child) => {
					// If "includeChildren" is ticked
					if (selection.includeChildren) {
						// and no "excludes" then tick
						if ((!selection.exclusion) || selection.exclusion.length === 0) {
							child.includeChildren = true;
						} else {
							// else if this is not excluded then tick
							child.includeChildren = selection.exclusion.every((exclusion) => {
								return exclusion.code !== child.code;
							});
						}

					}


					let childNode : ExclusionTreeNode = {
						codeSetValue : child
					} as ExclusionTreeNode;


					rootNode.children.push(childNode);
				});

				vm.exclusionTreeData = [ rootNode ];

					console.log(vm.exclusionTreeData);

			});
	}

	tickNode(node : ExclusionTreeNode) {
		console.log(node);
		if (node.codeSetValue.code === this.highlightedSelection.code) {
			// Ticking root so empty exclusions and tick all children
			this.highlightedSelection.exclusion = [];
			this.highlightedSelection.includeChildren = true;
			node.children.forEach((item) => { item.codeSetValue.includeChildren = true; });
		} else {
			if (this.highlightedSelection.includeChildren) {
				// Ticking an excluded child so find the exclusion...
				let index = this.findWithAttr(this.highlightedSelection.exclusion, 'code', node.codeSetValue.code);
				if (index > -1) {
					// ...remove it...
					this.highlightedSelection.exclusion.splice(index, 1);
					// ...tick it...
					node.codeSetValue.includeChildren = true;
					// ...and if no exclusions are left then set as "include all" at root
					if (this.highlightedSelection.exclusion.length === 0) {
						this.highlightedSelection.includeChildren = true;
					}
				}
			} else {
				// Ticking a child on "DONT include children" so tick root...
				this.highlightedSelection.includeChildren = true;
				// ...tick the node...
				node.codeSetValue.includeChildren = true;
				// ...and add the rest as exclusions
				this.highlightedSelection.exclusion = [];
				this.exclusionTreeData[0].children.forEach((childNode) => {
					if (childNode !== node) {
						this.highlightedSelection.exclusion.push(childNode.codeSetValue);
					}
				});
			}
		}
	}

	untickNode(node : ExclusionTreeNode) {
		if (node.codeSetValue.code === this.highlightedSelection.code) {
			// Unticking root so untick all children...
			node.children.forEach((item) => { item.codeSetValue.includeChildren = false; });
			// ... and clear exclusions list
			this.highlightedSelection.exclusion = [];
		} else {
			// Unticking a child so...
			if (this.highlightedSelection.exclusion == null) {
				// Initialize exclusion array if required
				this.highlightedSelection.exclusion = [];
			}
			// ...add exclusion
			this.highlightedSelection.exclusion.push(node.codeSetValue);
		}
		// Untick the node
		node.codeSetValue.includeChildren = false;
	}

	findWithAttr(array : any[], attr : string, value : string) : number {
		for (let i = 0; i < array.length; i += 1) {
			if (array[i][attr] === value) {
				return i;
			}
		}
		return -1;
	}

	ok() {
		this.activeModal.close(this.resultData);
		console.log('OK Pressed');
	}

	cancel() {
		this.activeModal.dismiss('cancel');
		console.log('Cancel Pressed');
	}
}
