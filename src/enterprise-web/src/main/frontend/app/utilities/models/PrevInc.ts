import {Organisation} from "../../report/models/Organisation";
import {Msoa} from "../../cohort/models/Msoa";
import {Lsoa} from "../../cohort/models/Lsoa";
export class PrevInc {
	organisation: Organisation[];
	population: string;
	codeSet: string;
	timePeriodNo: string;
	timePeriod: string;
	title: string;
	diseaseCategory: string;
	postCodePrefix: string;
	lsoaCode: Lsoa[];
	msoaCode: Msoa[];
	sex: string;
	ethnicity: string[];
	orgType: string;
	ageFrom: string;
	ageTo: string;
    dateType: string;
}