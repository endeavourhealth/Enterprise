module app.models {
	'use strict';

	export class TermlexTreeNode {
		id: string;
		label: string;
		childCount: number;
		isExpanded: boolean;
		nodes: TermlexTreeNode[];
	}
}