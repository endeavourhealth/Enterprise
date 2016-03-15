module app.models {
	export class CodeSelectionMatches {
		term:string;
		code:string;
	}

	export class CodeSelection {
		term:string;
		matches:CodeSelectionMatches[];
	}
}