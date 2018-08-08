import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {ItemTypeIdToStringPipe, ItemTypeIdToIconPipe} from "./ItemType.pipes";
import {LibraryComponent} from "./library.component";
import {CohortModule} from "../cohort/cohort.module";
import {CohortService} from "../cohort/cohort.service";
import {FolderModule, LibraryModule} from "eds-common-js";
import {ReportModule} from "../report/report.module";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,

		ReportModule,
		CohortModule,
		FolderModule,
		LibraryModule,
		],
	declarations:[
		LibraryComponent,

		ItemTypeIdToStringPipe,
		ItemTypeIdToIconPipe,
	],
	exports:[
		ItemTypeIdToStringPipe,
		ItemTypeIdToIconPipe,
	],
	providers : [
		CohortService
	]
})
export class EnterpriseLibraryModule {}