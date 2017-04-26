import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {ReportEditComponent} from "./reportEditor.component";

@NgModule({
	imports: [
		BrowserModule,
		FormsModule,
	],
	declarations: [
		ReportEditComponent,
	],
})
export class ReportModule {}