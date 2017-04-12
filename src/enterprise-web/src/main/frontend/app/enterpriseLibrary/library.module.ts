import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {ItemTypeIdToStringPipe, ItemTypeIdToIconPipe} from "./ItemType.pipes";
import {LibraryComponent} from "./library.component";
import {ReportsModule} from "../reports/reports.module";
import {ReportService} from "../reports/report.service";
import {FolderModule, LibraryModule} from "eds-common-js";


@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,

		ReportsModule,
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
		ReportService
	]
})
export class EnterpriseLibraryModule {}