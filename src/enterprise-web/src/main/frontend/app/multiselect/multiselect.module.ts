import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {MultiSelectDropdownComponent} from "./multiSelectDropdown.component";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,
	],
	declarations : [
		MultiSelectDropdownComponent
	],
	exports : [
		MultiSelectDropdownComponent
	],
})
export class MultiselectModule {}