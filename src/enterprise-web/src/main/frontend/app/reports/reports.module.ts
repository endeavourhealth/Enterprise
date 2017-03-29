import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {ReportService} from "./report.service";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";

import {ReportEditDialog} from "./reportEditor.dialog";
import {ReportViewDialog} from "./reportViewer.dialog";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule
	],
	declarations : [
		ReportEditDialog,
		ReportViewDialog
	],
	entryComponents : [
		ReportEditDialog,
		ReportViewDialog
	],
	providers : [
		ReportService,
	]
})
export class ReportsModule {}