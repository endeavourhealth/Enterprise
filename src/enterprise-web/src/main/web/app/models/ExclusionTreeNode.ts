module app.models {
	'use strict';

	export class ExclusionTreeNode extends CodeSetValue {
		children : ExclusionTreeNode[];
	}
}