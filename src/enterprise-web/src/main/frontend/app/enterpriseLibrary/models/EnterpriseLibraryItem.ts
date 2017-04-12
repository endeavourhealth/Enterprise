import {CodeSet} from "../../codeSet/models/CodeSet";
import {Query} from "../../query/models/Query";
import {LibraryItem} from "eds-common-js/dist/library/models/LibraryItem";

export class EnterpriseLibraryItem extends LibraryItem {
	query:Query;
	codeSet:CodeSet;
}
