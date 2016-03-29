module app.models {
    'use strict';

    export class CodeSetValueWithTerm {
        code : string;
        term : string;
        includeChildren : boolean;
        exclusion : CodeSetValueWithTerm[];
    }
}