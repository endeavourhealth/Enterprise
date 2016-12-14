import {CalculationType} from "../../library/models/CalculationType";
import {FieldTest} from "./FieldTest";
import {Restriction} from "../../expressions/models/Restriction";

export class DataSource {
	entity : string;
	dataSourceUuid : string[];
	calculation : CalculationType;
	filter : FieldTest[];
	restriction : Restriction;
}
