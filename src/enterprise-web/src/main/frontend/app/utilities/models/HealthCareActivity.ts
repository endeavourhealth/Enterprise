import {Msoa} from "../../cohort/models/Msoa";
import {Lsoa} from "../../cohort/models/Lsoa";

export class HealthCareActivity {
	organisationGroup: number;
	population: string;
	timePeriodNo: string;
	timePeriod: string;
	title: string;
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