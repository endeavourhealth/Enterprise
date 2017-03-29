import {CodeSet} from "../../codeSet/models/CodeSet";
import {Query} from "../../query/models/Query";

export class LibraryItem {
	uuid:string;
	name:string;
	description:string;
	folderUuid:string;
	query:Query;
	codeSet:CodeSet;

}
