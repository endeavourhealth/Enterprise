module app.models {
	'use strict';

	export class LibraryItem {
		uuid:string;
		name:string;
		description:string;
		folderUuid:string;
		codeSet:CodeSet;
	}
}