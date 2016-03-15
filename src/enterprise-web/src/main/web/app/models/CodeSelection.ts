module app.models {
	export class CodeSelectionMatch {
		term:string;
		code:string;
	}

	export class CodeSelection {
		term:string;
		includeChildren:boolean;
		matches:CodeSelectionMatch[];
	}
}