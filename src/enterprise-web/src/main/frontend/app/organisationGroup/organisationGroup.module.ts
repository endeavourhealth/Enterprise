import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {OrgGroupPickerComponent} from "./orgGroupPicker.component";
import {OrganisationGroupService} from "./organisationGroup.service";
@NgModule({
    imports : [
        BrowserModule,
        FormsModule,
        NgbModule
    ],
    declarations : [
        OrgGroupPickerComponent
    ],
    entryComponents : [
        OrgGroupPickerComponent
    ],
    providers : [
        OrganisationGroupService
    ]
})
export class OrganisationGroupModule {}