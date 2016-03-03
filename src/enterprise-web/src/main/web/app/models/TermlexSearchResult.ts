module app.models {
	'use strict';

	export class TermlexSearchResultCategory {
		count: number;
		type: string;
	}

	export class TermlexSearchResultResult {
		dt: string;
		id: string;
		label: string;
		lang: string;
		matches: string[];
		status: number;
		type: string;
	}

	export class TermlexSearchResult {
		categories: TermlexSearchResultCategory[];
		results: TermlexSearchResultResult[];
		searchTime: number;
		showingSuggestions: boolean;
		totalHits:number;
	}
}