module app.models {
	'use strict';

	export class ExclusionTreeNode extends CodeSelection {
		children : ExclusionTreeNode[];
	}
}