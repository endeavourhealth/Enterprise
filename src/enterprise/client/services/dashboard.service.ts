/// <reference path="../lib/angular/angular.d.ts" />
module Dashboard
{
    export interface IDashboardService {
        getEngineHistoryData() : string;
        getRecentDocumentsData() : string;
        getEngineState() : string;
        getReportActivityData() : string;
    }

    class DashboardService implements IDashboardService
    {
        static $inject = ["$http"];

        constructor(protected $http: ng.IHttpService) {
        }

        getEngineHistoryData() : string {
            return JSON.parse(`[
              {
                "History": {
                  "Outcome": "Success",
                  "Datetime": "08-Dec-2015"
                }
              },
              {
                "History": {
                  "Outcome": "Success",
                  "Datetime": "07-Dec-2015"
                }
              },
              {
                "History": {
                  "Outcome": "Success",
                  "Datetime": "06-Dec-2015"
                }
              }
            ]`);
        }
        getRecentDocumentsData() : string {
            return JSON.parse(`[
              {
                "Document": {
                  "Name": "Diabetic Review",
                  "Modified": "07-Dec-2015",
                  "Type": "Report"
                }
              },
              {
                "Document": {
                  "Name": "Asthmatics",
                  "Modified": "07-Dec-2015",
                  "Type": "Query"
                }
              },
              {
                "Document": {
                  "Name": "Asthma Review Required",
                  "Modified": "06-Dec-2015",
                  "Type": "Query"
                }
              },
              {
                "Document": {
                  "Name": "Cancer Indicators",
                  "Modified": "04-Dec-2015",
                  "Type": "Codeset"
                }
              },
              {
                "Document": {
                  "Name": "New Problems",
                  "Modified": "04-Dec-2015",
                  "Type": "Query"
                }
              }
            ]
            `);
        }
        getEngineState() : string {
            return JSON.parse(`{
              "EngineState": {
                "State": "Idle"
              }
            }`);
        }
        getReportActivityData() : string {
            return JSON.parse(`[
              {
                "Activity": {
                  "Name": "Diabetic Review",
                  "Rundate": "08-Dec-2015"
                }
              },
              {
                "Activity": {
                  "Name": "Diabetic Review",
                  "Rundate": "07-Dec-2015"
                }
              }
            ]`);
        }
    }

    angular
        .module("Dashboard")
        .service("DashboardService", DashboardService)
}