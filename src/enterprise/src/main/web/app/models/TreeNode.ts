module app.models {
	'use strict';

	export class TreeNode {
		uuid:string;
		folderName:string;
		folderType:number;
		hasChildren:boolean;
		contentCount:number;

		isExpanded:boolean;
		nodes:TreeNode[];
	}
}