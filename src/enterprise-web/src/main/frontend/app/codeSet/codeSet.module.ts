import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";

import {CodeSetEditComponent} from "./codeSetEditor.component";
import {CodingModule} from "../coding/coding.module";

@NgModule({
	imports: [
		BrowserModule,
		FormsModule,

		CodingModule,
	],
	declarations: [
		CodeSetEditComponent,
	],
})
export class CodeSetModule {}