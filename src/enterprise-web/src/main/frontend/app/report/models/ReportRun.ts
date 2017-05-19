import {Organisation} from "./Organisation";
export class ReportRun {
	organisation: Organisation[];
	population: string;
	baselineCohortId: string;
	baselineDate: string;
	reportItemUuid: string;
	scheduled: boolean;
	scheduleDateTime: Date;
}