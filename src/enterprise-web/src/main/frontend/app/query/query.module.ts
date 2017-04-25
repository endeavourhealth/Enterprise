import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";

import {QueryEditComponent} from "./queryEditor.component";
import {QueryPickerDialog} from "./queryPicker.dialog";
import {TestsModule} from "../tests/tests.module";
import {ExpressionsModule} from "../expressions/expressions.module";
import {EnterpriseLibraryModule} from "../enterpriseLibrary/library.module";
import {DialogsModule, FolderModule, LoggerModule} from "eds-common-js";
import {FlowchartModule} from "../flowChart/flowchart.module";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,
		LoggerModule,
		FlowchartModule,
		FolderModule,
		EnterpriseLibraryModule,
		DialogsModule,
		ExpressionsModule,
		TestsModule
	],
	declarations : [
		QueryEditComponent,
		QueryPickerDialog
	],
	entryComponents : [
		QueryPickerDialog
	]
})
export class QueryModule {}