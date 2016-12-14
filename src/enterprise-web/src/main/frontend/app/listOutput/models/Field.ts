import {DataValueType} from "../../library/models/DataValueType";
import {LogicalDataType} from "../../library/models/LogicalDataType";
export class Field {
	logicalName : string;
	displayName : string;
	index : number;
	availability : string[];
	logicalDataType : LogicalDataType;
	dataValues : DataValueType[];
}
