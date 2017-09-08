 export class Series {
	type : string;
	name : string;
	data : any[] = [];
	size : number;
	center: number[] = [];
	yAxis : number;
	showInLegend : boolean;
	visible : boolean;

	public setType(type : string) : Series {
		this.type = type;
		return this;
	}

	public setName(name : string) : Series {
		this.name = name;
		this.showInLegend = (name != null && name != '');
		return this;
	}

	public setData(data : any[]) : Series {
		this.data = data;
		return this;
	}

	public addData(data : any) : Series {
		this.data.push(data);
		return this;
	}

	public setSize(size : number) : Series {
		this.size = size;
		return this;
	}

	public setCenter(x, y : number) : Series {
		this.center = [x,y];
		return this;
	}

	public setyAxis(yAxis : number) : Series {
		this.yAxis = yAxis;
		return this;
	}

	public setVisible(visible : boolean) : Series {
		this.visible = visible;
		return this;
	}
 }