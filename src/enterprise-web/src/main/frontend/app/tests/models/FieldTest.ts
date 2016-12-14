import {CodeSet} from "../../codeSet/models/CodeSet";
import {ValueFrom} from "./ValueFrom";
import {ValueTo} from "./ValueTo";
import {ValueRange} from "./ValueRange";
import {Value} from "./Value";
import {ValueSet} from "./ValueSet";

export class FieldTest {
	field : string;
	valueFrom : ValueFrom;
	valueTo : ValueTo;
	valueRange : ValueRange;
	valueEqualTo : Value;
	codeSet : CodeSet;
	valueSet: ValueSet;
	codeSetLibraryItemUuid : string[];
	negate : boolean;
}
