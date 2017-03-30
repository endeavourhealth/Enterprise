import {SecurityService} from "../security/security.service";
import {User} from "../users/models/User";
import {Component} from "@angular/core";

@Component({
	selector: 'topnav-component',
	template: require('./topnav.html')
})
export class TopnavComponent {
	currentUser:User;

	constructor(private securityService:SecurityService) {
		this.getCurrentUser();
	}

	getCurrentUser() {
		this.currentUser = this.securityService.getCurrentUser();
	}

	navigateUserAccount() {
		var url = window.location.protocol + "//" + window.location.host;
		url = url + "/eds-user-manager/#/app/users/userManagerUserView";
		window.location.href = url;
	}

	logout() {
		this.securityService.logout();
	};
}
