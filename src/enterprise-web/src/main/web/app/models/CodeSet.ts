module app.models {
	'use strict';

	export class CodeSet {
		codingSystem : CodingSystem;
		codeSetValue : CodeSetValue[];
	}
}