module app.models {
	'use strict';

	export class Value {
		constant : string;
		parameter : ParameterType;
		absoluteUnit : ValueAbsoluteUnit;
		relativeUnit : ValueRelativeUnit;
	}
}