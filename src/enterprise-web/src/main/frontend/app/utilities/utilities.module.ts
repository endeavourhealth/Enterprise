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
import {IndDashChartDialog} from "./indDash/indDashChart.dialog";
import {MultiselectModule} from "../multiselect/multiselect.module";
import {PrevIncService} from "./prevInc/prevInc.service";
import {IndDashService} from "./indDash/indDash.service";
import {HealthCareActivityService} from "./healthCareActivity/healthCareActivity.service";
import {HealthCareActivityChart} from "./healthCareActivity/healthCareActivityChart.dialog";
import {OrganisationGroupModule} from "../organisationGroup/organisationGroup.module";
import {TreeModule} from "angular2-tree-component";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		TreeModule,
		NgbModule,
		ChartModule,
		ControlsModule,
		MultiselectModule,
		OrganisationGroupModule
	],
	declarations : [
		UtilitiesComponent,
		PrevIncDialog,
		PrevIncChartDialog,
		IndDashChartDialog,
		HealthCareActivityDialog,
		HealthCareActivityChart
	],
	entryComponents : [
		PrevIncDialog,
		PrevIncChartDialog,
		IndDashChartDialog,
		HealthCareActivityDialog,
		HealthCareActivityChart
	],
	providers : [
		UtilitiesService,
		PrevIncService,
		IndDashService,
		HealthCareActivityService
	]
})
export class UtilitiesModule {}