import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {DashboardService} from "./dashboard.service";
import {DashboardComponent} from "./dashboard.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {LibraryModule} from "../library/library.module";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,

		LibraryModule,
	],
	declarations : [
		DashboardComponent
	],
	providers : [
		DashboardService
	]
})
export class DashboardModule {}