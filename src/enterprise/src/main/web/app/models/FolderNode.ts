module app.models {
	'use strict';

	export class FolderNode {
		uuid:string;
		folderName:string;
		folderType:number;
		hasChildren:boolean;
		contentCount:number;

		isExpanded:boolean;
		isSelected:boolean;
		nodes:FolderNode[];
	}
}