import {Rule} from "../../library/models/Rule";
import {StartingRules} from "./StartingRules";
export class Query {
    parentQueryUuid  : string;
    startingRules : StartingRules;
    rule : Rule[];
}
