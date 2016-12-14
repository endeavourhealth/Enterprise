export class UuidNameKVP {
	uuid : string;
	name : string;

	static toAssociativeArray(items : UuidNameKVP[]) {
		let associativeArray = {};
		for (let i = 0; i < items.length; i++) {
			associativeArray[items[i].uuid] = items[i].name;
		}

		return associativeArray;
	}

	static fromAssociativeArray(associativeArray : Object) {
		let array : UuidNameKVP[] = [];
		for (let key in associativeArray) {
			if (associativeArray.hasOwnProperty(key)) {
				array.push({uuid: key, name: associativeArray[key]});
			}
		}

		return array;
	}
}
