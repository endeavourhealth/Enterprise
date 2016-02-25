module app.models {
	export class FolderContent {
		uuid:string;
		type:ItemType;
		name:string;
		lastModified:number;
		lastRun:number; //only applicable when showing reports
		isScheduled:boolean; //only applicable when showing reports
	}
}