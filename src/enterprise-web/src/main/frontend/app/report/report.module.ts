import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {ReportEditComponent} from "./reportEditor.component";
import {ReportRunnerDialog} from "./reportRunner.dialog";

@NgModule({
	imports: [
		BrowserModule,
		FormsModule,
	],
	declarations: [
		ReportEditComponent,
		ReportRunnerDialog
	],
	entryComponents: [
		ReportRunnerDialog
	]
})
export class ReportModule {}