import {Test} from "../../tests/models/Test";
import {ExpressionType} from "../../expressions/models/ExpressionType";
import {RuleAction} from "./RuleAction";
import {LayoutType} from "./LayoutType";

export class Rule {
    description : string;
    id : string;
    type : string;
    test : Test;
    testLibraryItemUUID : string;
    queryLibraryItemUUID : string;
    expression : ExpressionType;
    onPass : RuleAction;
    onFail : RuleAction;
    layout : LayoutType;
}
