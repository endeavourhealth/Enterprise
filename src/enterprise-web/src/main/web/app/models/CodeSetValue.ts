module app.models {
	'use strict';

	export class CodeSetValue {
		code : string;
		includeChildren : boolean;
		exclusions : CodeSetValue[];
	}
}