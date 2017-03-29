import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {TreeModule} from "angular2-tree-component";

import {CodePickerDialog} from "./codePicker.dialog";
import {TermlexCodingService} from "./termlex/termlexCoding.service";
import {CodingService} from "./coding.service";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		TreeModule,
		NgbModule
	],
	declarations : [
		CodePickerDialog
	],
	entryComponents : [
		CodePickerDialog
	],
	providers : [
		{ provide : CodingService, useClass : TermlexCodingService}
	],
})
export class CodingModule {}