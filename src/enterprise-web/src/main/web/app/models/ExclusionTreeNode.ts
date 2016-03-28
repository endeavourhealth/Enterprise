module app.models {
	'use strict';

	export class ExclusionTreeNode extends CodeSetValueWithTerm {
		children : ExclusionTreeNode[];
	}
}