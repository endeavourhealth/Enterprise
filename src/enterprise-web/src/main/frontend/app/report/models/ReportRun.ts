import {Organisation} from "./Organisation";
export class ReportRun {
	organisationGroup: string;
	population: string;
	baselineCohortId: string;
	cohortName: string;
	baselineDate: string;
	reportItemUuid: string;
	scheduled: boolean;
	scheduleDateTime: Date;

}