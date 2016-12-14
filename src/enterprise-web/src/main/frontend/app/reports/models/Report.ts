import {ReportItem} from "./ReportItem";
export class Report {
	uuid : string;
	name : string;
	description : string;
	folderUuid : string;
	reportItem : ReportItem[];
}
