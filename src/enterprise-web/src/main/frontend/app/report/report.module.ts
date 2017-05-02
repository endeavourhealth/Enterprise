import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {ReportEditComponent} from "./reportEditor.component";
import {ReportRunnerDialog} from "./reportRunner.dialog";
import {ReportService} from "./report.service";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";

@NgModule({
	imports: [
		BrowserModule,
		FormsModule,
		NgbModule
	],
	declarations: [
		ReportEditComponent,
		ReportRunnerDialog
	],
	entryComponents: [
		ReportRunnerDialog
	],
	providers : [
		ReportService
	]
})
export class ReportModule {}