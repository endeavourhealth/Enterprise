import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {ItemTypeIdToStringPipe, ItemTypeIdToIconPipe} from "./ItemType.pipes";
import {LibraryComponent} from "./library.component";
import {LibraryService} from "./library.service";
import {FolderModule} from "../folder/folder.module";
import {ReportsModule} from "../reports/reports.module";
import {ReportService} from "../reports/report.service";


@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,
		ReportsModule,
		FolderModule,

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
		LibraryService,
		ReportService
	]
})
export class LibraryModule {}