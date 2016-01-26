/// <reference path="../../typings/tsd.d.ts" />

module app.library {
    export class EngineHistoryItem {
        Datetime : string;
        Outcome : string;
    }

    export class RecentDocumentItem {
        Name : string;
        Modified : string;
        Type : String;
    }

    export class EngineState {
        State : string;
    }

    export class ReportActivityItem {
        Name : string;
        Rundate : string;
    }
}