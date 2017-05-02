import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {CohortService} from "./cohort.service";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";

import {CohortEditDialog} from "./cohortEditor.dialog";
import {CohortViewDialog} from "./cohortViewer.dialog";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule
	],
	declarations : [
		CohortEditDialog,
		CohortViewDialog
	],
	entryComponents : [
		CohortEditDialog,
		CohortViewDialog
	],
	providers : [
		CohortService,
	]
})
export class CohortModule {}