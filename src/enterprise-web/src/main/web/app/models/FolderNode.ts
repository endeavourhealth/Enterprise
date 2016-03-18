module app.models {
	'use strict';

	export class FolderNode {
		uuid:string;
		folderName:string;
		folderType:number;
		parentFolderUuid:string;
		hasChildren:boolean;
		contentCount:number;

		isExpanded:boolean;
		loading:boolean;
		nodes:FolderNode[];
	}
}