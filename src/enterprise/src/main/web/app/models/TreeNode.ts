module app.models {
	'use strict';

	export class TreeNode {
		itemUuid:string;
		title:string;
		hasChildren:boolean;
		isExpanded:boolean;
		nodes:TreeNode[];
	}
}