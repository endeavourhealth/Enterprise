import {DataType} from "../../codeSet/models/DataType";

export class TermlexCode {
	id: string;
	label: string;
	dataType : DataType;
	parentType : string;
	baseType : string;
	present : string;
	valueFrom : string;
	valueTo : string;
	units : string;
}
