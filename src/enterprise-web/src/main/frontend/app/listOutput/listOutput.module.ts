import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {ListReportGroupToIcon, ListReportGroupToTypeName, ListReportGroupToDescription} from "./ListReportGroup.pipes";
import {ListOutputEditComponent} from "./listOutputEdit.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
@NgModule({
	imports : [
		BrowserModule,
		FormsModule,
		NgbModule,
	],
	declarations : [
		ListOutputEditComponent,

		ListReportGroupToIcon,
		ListReportGroupToTypeName,
		ListReportGroupToDescription,
	],
	exports : [
		ListReportGroupToIcon,
		ListReportGroupToTypeName,
		ListReportGroupToDescription,
	]
})
export class ListOutputModule {}