import {DataType} from "./DataType";

export class CodeSetValue {
	code : string;
	term : string;
	dataType : DataType;
	parentType : string;
	baseType : string;
	present : string;
	valueFrom : string;
	valueTo : string;
	units : string;
	includeChildren : boolean;
	exclusion : CodeSetValue[];
}

