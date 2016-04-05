 module app.models {
	'use strict';

	export class QueryResult {
		name : string;
		description : string;
		uuid : string;
		resultCount : number;
		percentageOfParent : number;
		childQueries : QueryResult[];
	}
}