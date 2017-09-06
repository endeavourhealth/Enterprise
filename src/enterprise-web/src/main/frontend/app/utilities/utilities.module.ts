import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {UtilitiesService} from "./utilities.service";
import {UtilitiesComponent} from "./utilities.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {PrevIncDialog} from "./prevInc.dialog";
import {ChartModule} from "../charting/chart.module";
import {ControlsModule} from "eds-common-js";
import {PrevIncChartDialog} from "./prevIncChart.dialog";
import {MultiselectDropdownModule} from "angular-2-dropdown-multiselect";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,
		ChartModule,
		ControlsModule,
		MultiselectDropdownModule
	],
	declarations : [
		UtilitiesComponent,
		PrevIncDialog,
		PrevIncChartDialog
	],
	entryComponents : [
		PrevIncDialog,
		PrevIncChartDialog
	],
	providers : [
		UtilitiesService
	]
})
export class UtilitiesModule {}