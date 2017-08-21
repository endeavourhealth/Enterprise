import {Component} from "@angular/core";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {StateService} from "ui-router-ng2";
import {UtilitiesService} from "./utilities.service";
import {LoggerService} from "eds-common-js";
import {PrevIncDialog} from "./prevInc.dialog";
import {PrevInc} from "./models/PrevInc";

@Component({
	template : require('./utilities.html')
})
export class UtilitiesComponent {

	constructor(private utilitiesService:UtilitiesService,
							private logger:LoggerService,
							private $modal: NgbModal,
							private $state : StateService) {
	}

	pi () {
		let vm = this;
		let prevInc: PrevInc = {
			organisation: [],
			population: ""
		};
		PrevIncDialog.open(vm.$modal, prevInc).result.then(
			(result) => {
				console.log(result);
			},
			(error) => vm.logger.error("Error running utility", error)
		);
	}

}

