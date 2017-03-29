import {CodeSet} from "../../codeSet/models/CodeSet";
import {ValueFrom} from "./ValueFrom";
import {ValueTo} from "./ValueTo";
import {Value} from "./Value";
import {ValueSet} from "./ValueSet";

export class Filter {
	field : string;
	valueFrom : ValueFrom;
	valueTo : ValueTo;
	codeSet : CodeSet;
	valueSet: ValueSet;
	codeSetLibraryItemUuid : string[];
	negate : boolean;
}
