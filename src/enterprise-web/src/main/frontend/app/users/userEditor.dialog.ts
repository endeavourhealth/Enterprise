import {Component} from "@angular/core";
import {DialogBase} from "../dialogs/dialog.base";
import {User} from "./models/User";
import {NgbModal, NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
	selector: 'ngbd-modal-content',
	template: require('./userEditor.html')
})
export class UserEditDialog extends DialogBase {

	public static open(modalService: NgbModal, user : User) {
		const modalRef = modalService.open(UserEditDialog, { backdrop : "static"});
		modalRef.componentInstance.resultData = jQuery.extend(true, {}, user);

		return modalRef;
	}

	constructor(protected $uibModalInstance : NgbActiveModal) {
		super($uibModalInstance);
	}
}
