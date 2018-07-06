import {Input, Component} from "@angular/core";
import {NgbModal, NgbActiveModal, NgbTabChangeEvent} from "@ng-bootstrap/ng-bootstrap";
import {ITreeOptions} from "angular2-tree-component";
import {CodeSetValue} from "../codeSet/models/CodeSetValue";
import {Term} from "./models/Term";
import {TermService} from "./term.service";
import {LoggerService} from "eds-common-js";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./termPicker.html')
})
export class TermPickerDialog {

	public static open(modalService: NgbModal,   selection : CodeSetValue[]) {
		const modalRef = modalService.open(TermPickerDialog, { backdrop : "static", size : "lg"});
		modalRef.componentInstance.resultData = selection;

		return modalRef;
	}

	@Input() resultData;
	options : ITreeOptions;

	highlightedMatch : Term;
	previousSelection : Term;
	highlightedSelection : Term;

	searchTerm : string;
	searchResults : Term[];
	searchTerms : Term[];
	parents : CodeSetValue[];
	children : CodeSetValue[];

	termCache : any;

	readInclusions : string;
	readExclusions : string;
    readResults : CodeSetValue[];

	constructor(private logger : LoggerService,
				protected activeModal : NgbActiveModal,
				private termService : TermService) {
		this.termCache = {};
		this.options = {
			childrenField : 'children',
			idField : 'code'
		};

	}

	search() {
		let vm = this;
		vm.termService.getTerms(vm.searchTerm)
			.subscribe(
				(result) => {
					console.log(result);
				vm.searchTerms = result;
				vm.searchResults = [];
				vm.parents = [];
				vm.children = [];
			});
	}

	displayCode(itemToDisplay : Term, replace : boolean) {
		let vm = this;

		if (vm.highlightedMatch) {
			vm.previousSelection = vm.highlightedMatch;
		}

		if (replace) {
			vm.searchResults = [itemToDisplay];
		}

		vm.highlightedMatch = itemToDisplay;


	}

	addToSelection(match : Term, includeChildren : boolean = true) {

		console.log(match);
		for (var i = 0; i < this.resultData.length; ++i) {
			var baseType = this.resultData[i].baseType;

			if (baseType!=match.recordType) {
				this.logger.error('Each rule must have concepts of the same base type (i.e. Patient and Observation concepts cannot be mixed in the same rule)');
				return;

			}
		}

		let item : CodeSetValue = {
			code : match.snomedConceptId,
            includeChildren : includeChildren,
			term : match.originalTerm,
			dataType : 11,
			parentType : '',
			baseType : match.recordType,
			present : '1',
			valueFrom : '',
			valueTo : '',
			units : '',
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

	ok() {
		this.activeModal.close(this.resultData);
		console.log('OK Pressed');
	}

	cancel() {
		this.activeModal.dismiss('cancel');
		console.log('Cancel Pressed');
	}


}
