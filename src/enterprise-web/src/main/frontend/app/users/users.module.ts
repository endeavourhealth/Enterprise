import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {UserEditDialog} from "./userEditor.dialog";
import {UserListComponent} from "./userList.component";
import {UserService} from "./user.service";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule
	],
	declarations : [
		UserListComponent,
		UserEditDialog
	],
	entryComponents : [
		UserEditDialog
	],
	providers : [
		UserService
	]
})
export class UsersModule {}