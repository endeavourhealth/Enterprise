import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {ChartComponent} from "./chart.component";
import {ChartDialog} from "./chart.dialog";
import {TableDialog} from "./table.dialog";
import {TableComponent} from "./table.component";
import {DualDialog} from "./dual.dialog";

@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,
	],
	declarations : [
		ChartComponent,
		TableComponent,
		ChartDialog,
		TableDialog,
		DualDialog,
	],
	exports : [
		ChartComponent,
		TableComponent,
		ChartDialog,
		TableDialog,
		DualDialog
	],
	entryComponents : [
		ChartDialog,
		TableDialog,
		DualDialog
	]
})
export class ChartModule {}