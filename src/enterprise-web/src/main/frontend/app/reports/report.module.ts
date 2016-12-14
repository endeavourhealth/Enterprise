import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {ReportEditComponent} from "./reportEdit.component";
import {ReportService} from "./report.service";
import {QueueReportDialog} from "./queueReport.dialog";
import {ReportListComponent} from "./reportList.component";
import {FolderModule} from "../folder/folder.module";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {LibraryModule} from "../library/library.module";
import {TreeModule} from "angular2-tree-component";
import {AdminModule} from "../admin/admin.module";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,
		TreeModule,

		FolderModule,
		LibraryModule,
		AdminModule,
	],
	declarations : [
		ReportListComponent,
		ReportEditComponent,
		QueueReportDialog
	],
	entryComponents : [
		QueueReportDialog
	],
	providers : [
		ReportService
	]
})
export class ReportsModule {}