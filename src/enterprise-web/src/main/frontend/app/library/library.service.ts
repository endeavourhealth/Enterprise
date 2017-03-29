import {Injectable} from "@angular/core";
import {Http, URLSearchParams} from "@angular/http";
import {BaseHttp2Service} from "../core/baseHttp2.service";
import {Observable} from "rxjs";
import {LibraryItem} from "./models/LibraryItem";
import {ItemSummaryList} from "./models/ItemSummaryList";

@Injectable()
export class LibraryService extends BaseHttp2Service {
	constructor(http : Http) { super(http); }

	getFolderContents(folderUuid : string):Observable<ItemSummaryList> {
		let params = new URLSearchParams();
		params.append('folderUuid', folderUuid);
		return this.httpGet('api/folder/getFolderContents', { search : params });
	}

	getLibraryItem(uuid : string):Observable<LibraryItem> {
		var params : URLSearchParams = new URLSearchParams();
		params.append('uuid', uuid);

		return this.httpGet('api/library/getLibraryItem', {search : params});
	}

	saveLibraryItem(libraryItem : LibraryItem):Observable<LibraryItem> {
		return this.httpPost('api/library/saveLibraryItem', libraryItem);
	}

	deleteLibraryItem(uuid : string):Observable<any> {
		var request = {
			uuid : uuid
		};
		return this.httpPost('api/library/deleteLibraryItem', request);
	}



}
