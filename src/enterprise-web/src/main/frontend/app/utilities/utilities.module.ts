import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {UtilitiesService} from "./utilities.service";
import {UtilitiesComponent} from "./utilities.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {PrevIncDialog} from "./prevInc/prevInc.dialog";
import {HealthCareActivityDialog} from "./healthCareActivity/healthCareActivity.dialog";
import {ChartModule} from "../charting/chart.module";
import {ControlsModule} from "eds-common-js";
import {PrevIncChartDialog} from "./prevInc/prevIncChart.dialog";
import {OrgGroupPickerComponent} from "./orgGroupPicker.component";
import {MultiselectModule} from "../multiselect/multiselect.module";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,
		ChartModule,
		ControlsModule,
		MultiselectModule
	],
	declarations : [
		UtilitiesComponent,
		PrevIncDialog,
		HealthCareActivityDialog,
		PrevIncChartDialog,
		OrgGroupPickerComponent
	],
	entryComponents : [
		PrevIncDialog,
		HealthCareActivityDialog,
		PrevIncChartDialog,
		OrgGroupPickerComponent
	],
	providers : [
		UtilitiesService
	]
})
export class UtilitiesModule {}