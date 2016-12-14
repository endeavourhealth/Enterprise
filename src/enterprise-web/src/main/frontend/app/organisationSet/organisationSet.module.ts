import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {OrganisationSetService} from "./organisationSet.service";
import {OrganisationSetComponent} from "./organisationSet.component";
import {OrganisationSetPickerDialog} from "./organisationPicker.dialog";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule
	],
	declarations : [
		OrganisationSetComponent,
		OrganisationSetPickerDialog,
	],
	entryComponents : [
		OrganisationSetPickerDialog,
	],
	providers : [
		OrganisationSetService,
	]
})
export class OrganisationSetModule {}