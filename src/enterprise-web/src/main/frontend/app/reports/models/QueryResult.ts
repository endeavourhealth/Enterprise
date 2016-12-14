export class QueryResult {
	name : string;
	description : string;
	uuid : string;
	resultCount : number;
	percentageOfParent : string;
	childQueries : QueryResult[];
}
