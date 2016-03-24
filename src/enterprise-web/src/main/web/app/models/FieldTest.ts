module app.models {
	'use strict';

	export class FieldTest {
		field : string;
		valueFrom : ValueFrom;
		valueTo : ValueTo;
		valueRange : ValueRange;
		valueEqualTo : Value;
		codeSet : CodeSet[];
		codeSetLibraryItemUuid : string[];
		negate : boolean;
	}
}