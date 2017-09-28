import {Organisation} from "../../report/models/Organisation";

export class OrganisationGroup {
    id : number;
    name : string;
    organisations?: Organisation[]
}