import {OnInit, Input, Component, ViewChild} from "@angular/core";
import {NgbModal, NgbActiveModal, NgbTabChangeEvent} from "@ng-bootstrap/ng-bootstrap";
import {ITreeOptions, TreeComponent} from "angular2-tree-component";
import {CodeSetValue} from "../codeSet/models/CodeSetValue";
import {Term} from "./models/Term";
import {TermService} from "./term.service";
import {LoggerService} from "eds-common-js";
import {ExclusionTreeNode} from "./models/ExclusionTreeNode";
import {InclusionTreeNode} from "./models/InclusionTreeNode";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./termPicker.html')
})
export class TermPickerDialog implements OnInit{

	public static open(modalService: NgbModal,   selection : CodeSetValue[]) {
		const modalRef = modalService.open(TermPickerDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.resultData = selection;

		return modalRef;
	}

	@Input() resultData;
	options : ITreeOptions;

	highlightedMatch : CodeSetValue;
	previousSelection : CodeSetValue;
	highlightedSelection : CodeSetValue;

	snomed: string = "";
	treeCount: number = 0;

	searchTerm : string;
	searchTerms : Term[];
	parents : Term[];
	children : Term[];

	termCache : any;

	exclusionTreeData : ExclusionTreeNode[];
	inclusionTreeData : InclusionTreeNode[];

	selectedCodes : any[];

	@ViewChild("inclusionTree")
	inclusionTree: TreeComponent;

	constructor(private logger : LoggerService,
				protected activeModal : NgbActiveModal,
				private termService : TermService) {
		this.termCache = {};
		this.options = {
			childrenField : 'children',
			idField : 'id'
		};
	}

	ngOnInit(): void {
		this.inclusionTreeData = [];
		this.selectedCodes = [];

		for (const term of this.resultData) {
			let rootNode : InclusionTreeNode = {
				id : term.code,
				name : term.term,
				recordType : term.baseType,
				checked : true,
				indeterminate : false,
				children : []
			} as InclusionTreeNode;

			this.selectedCodes.push(term.code);
			this.inclusionTreeData.push(rootNode);
			this.inclusionTree.treeModel.update();
		}

	}

	search() {
		let vm = this;
		vm.termService.getTerms(vm.searchTerm, vm.snomed)
			.subscribe(
				(result) => {
					console.log(result);
				vm.searchTerms = result;
				vm.parents = [];
				vm.children = [];

			});
	}

	displayCode(itemToDisplay : Term, replace : boolean) {
		let vm = this;

		if (replace) {
			vm.searchTerms = [itemToDisplay];

		}

		vm.termService.getTermChildren(itemToDisplay.snomedConceptId)
			.subscribe(
				(result) => vm.children = result

			);

		vm.termService.getTermParents(itemToDisplay.snomedConceptId)
			.subscribe(
				(result) => vm.parents = result
			);



	}

	addChildren(match : Term, rootNode : InclusionTreeNode) {
		let vm = this;

		vm.termService.getTermChildren(match.snomedConceptId)
			.subscribe(
				(result) => {
					result.forEach((child) => {
						let childNode : InclusionTreeNode = {
							id : child.snomedConceptId,
							name : child.originalTerm,
							recordType : child.recordType,
							checked : true,
							indeterminate : false,
							children : []
						} as InclusionTreeNode;

						if (this.selectedCodes.indexOf(child.snomedConceptId)>-1)
							return;

						this.selectedCodes.push(child.snomedConceptId);
						rootNode.children.push(childNode);
						this.inclusionTree.treeModel.update();

						this.treeCount++;

						this.addChildren(child, childNode);

					});
				});
	}

	addToSelection(match : Term) {
		let vm = this;

		for (var i = 0; i < this.resultData.length; ++i) {
			var baseType = this.resultData[i].baseType;

			if (baseType!=match.recordType) {
				this.logger.error('Each rule must have concepts of the same base type (i.e. Patient and Observation concepts cannot be mixed in the same rule)');
				return;
			}
		}

		let rootNode : InclusionTreeNode = {
			id : match.snomedConceptId,
			name : match.originalTerm,
			recordType : match.recordType,
			checked : true,
			indeterminate : false,
			children : []
		} as InclusionTreeNode;

		this.addChildren(match, rootNode);

		if (this.selectedCodes.indexOf(match.snomedConceptId)>-1)
		{
			this.logger.error('Term already selected');
			return;
		}

		this.selectedCodes.push(match.snomedConceptId);
		this.inclusionTreeData.push(rootNode);
		this.inclusionTree.treeModel.update();

		this.treeCount++;

	}

	check(node, checked) {
		this.updateChildNodeCheckbox(node, checked);
		this.updateParentNodeCheckbox(node.realParent);
	}

	updateChildNodeCheckbox(node, checked) {
		node.data.checked = checked;
		if (node.children) {
			node.children.forEach((child) => this.updateChildNodeCheckbox(child, checked));
		}
	}

	updateParentNodeCheckbox(node) {
		if (!node) {
			return;
		}

		let allChildrenChecked = true;
		let noChildChecked = true;

		for (const child of node.children) {
			if (!child.data.checked || child.data.indeterminate) {
				allChildrenChecked = false;
			}
			if (child.data.checked) {
				noChildChecked = false;
			}
		}

		if (allChildrenChecked) {
			node.data.checked = true;
			node.data.indeterminate = false;
			node.indeterminate = false;
		} else if (noChildChecked) {
			node.data.checked = true;
			node.data.indeterminate = false;
			node.indeterminate = false;
		} else {
			node.data.checked = false;
			node.data.indeterminate = true;
			node.indeterminate = true;
		}
		this.updateParentNodeCheckbox(node.parent);
	}

	setResultData(node) {
		let item : CodeSetValue = {
			code : node.id,
			includeChildren : true,
			term : node.name,
			dataType : 11,
			parentType : '',
			baseType : node.recordType,
			present : '1',
			valueFrom : '',
			valueTo : '',
			units : '',
			exclusion : []
		};
		this.resultData.push(item);
	}

	updateResultData() {
		this.resultData = [];
		this.inclusionTreeData.forEach((node) => {
				this.updateChildren(node);
			}
		);
	}

	updateChildren(node) {
		if (node.checked||node.indeterminate)
			this.setResultData(node);
		if (node.children) {
			node.children.forEach((child) => this.updateChildren(child));
		}
	}

	ok() {
		this.updateResultData();
		console.log(this.resultData);

		this.activeModal.close(this.resultData);
		console.log('OK Pressed');
	}

	cancel() {
		this.activeModal.dismiss('cancel');
		console.log('Cancel Pressed');
	}


}
