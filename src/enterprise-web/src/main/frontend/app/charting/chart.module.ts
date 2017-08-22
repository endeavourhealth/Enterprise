import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {ChartComponent} from "./chart.component";
import {ChartDialog} from "./chart.dialog";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,
	],
	declarations : [
		ChartComponent,
		ChartDialog
	],
	exports : [
		ChartComponent,
		ChartDialog
	],
	entryComponents : [
		ChartDialog
	]
})
export class ChartModule {}