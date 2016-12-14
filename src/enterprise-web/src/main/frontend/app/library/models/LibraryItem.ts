import {CodeSet} from "../../codeSet/models/CodeSet";
import {Query} from "../../query/models/Query";
import {ListReport} from "../../listOutput/models/ListReport";

export class LibraryItem {
	uuid:string;
	name:string;
	description:string;
	folderUuid:string;
	query:Query;
	// dataSource:DataSource;
	// test:Test;
	codeSet:CodeSet;
	listReport:ListReport;
}
