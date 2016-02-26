module app.models {
	export class CodeSearchMatch {
		term:string;
		code:string;
	}

	export class CodeSearchResult {
		term:string;
		matches:CodeSearchMatch[];
	}
}