import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule} from '@angular/forms';
import {TreeModule} from 'angular2-tree-component';

import {CodePickerDialog} from './codePicker.dialog';
import {TermlexCodingService} from './termlex/termlexCoding.service';
import {CodingService} from './coding.service';
import {TermService} from './term.service';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {TermPickerDialog} from './termPicker.dialog';
import {ControlsModule} from 'eds-common-js';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        TreeModule,
        NgbModule,
        ControlsModule
    ],
    declarations: [
        CodePickerDialog, TermPickerDialog
    ],
    entryComponents: [
        CodePickerDialog, TermPickerDialog
    ],
    providers: [
        TermService, {provide: CodingService, useClass: TermlexCodingService}
    ],
})
export class CodingModule {
}