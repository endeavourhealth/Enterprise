import {Component} from "@angular/core";
import {User} from "./models/User";
import {LoggerService} from "../common/logger.service";
import {UserService} from "./user.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {UserEditDialog} from "./userEditor.dialog";

@Component({
	template : require('./userList.html')
})
export class UserListComponent {
	userType : string;
	userList : User[];

	constructor(private logger : LoggerService,
							private userService : UserService,
							private $modal : NgbModal) {
		this.userType = 'all';
		this.loadUsers();
	}

	getUserList() {
		// TODO : Sorting
		return this.userList;
	}

	editUser(user : User) {
		let vm = this;
		UserEditDialog.open(vm.$modal, user)
			.result.then(function(editedUser : User) {
				vm.userService.saveUser(editedUser)
					.subscribe(
						(response : {uuid : string}) => {
							editedUser.uuid = response.uuid;
							let i = vm.userList.indexOf(user);
							vm.userList[i] = editedUser;
							vm.logger.success('User saved', editedUser, 'Edit user');
						});
		});
	}

	viewUser(user : User) {
		let vm = this;
		UserEditDialog.open(vm.$modal, user);
	}

	deleteUser(user : User) {
		this.logger.error('Delete ' + user.username);
	}

	private loadUsers() {
		let vm = this;
		vm.userService.getUserList()
			.subscribe(
				(result) => vm.userList = result.users
			);
	}
}
