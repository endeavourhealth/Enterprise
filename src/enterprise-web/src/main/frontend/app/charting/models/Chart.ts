import {Series} from "./Series";

export class Chart {
	title : string;
	categories : string[] = [];
	series : Series[] = [];
	yAxis : any[] = [];
	colors : string[];
	height : number;
	legend : any = {};

	public setTitle(title : string) : Chart {
		this.title = title;
		return this;
	}

	public setCategories(categories : string[]) : Chart {
		this.categories = categories;
		return this;
	}

	public addCategory(category : string) : Chart {
		this.categories.push(category);
		return this;
	}

	public setSeries(series : Series[]) : Chart {
		this.series = series;
		return this;
	}

	public addSeries(series : Series) : Chart {
		this.series.push(series);
		return this;
	}

	public getRowData() : string [] {
		let header : string = 'Series,' + this.categories.join(',');

		let rows : string[] = this.series.map(
			series => series.name + ',' + series.data.join(',')
		);

		rows = [header].concat(rows);

		return rows;
	}

	public export() {
		let data = this.getRowData().join('\n');

		let blob = new Blob([data], { type: 'text/plain' });
		window['saveAs'](blob, this.title + '.csv');
	}

	public addYAxis(title : string, showRight : boolean) : Chart {
		this.yAxis.push(
			{
				title: {text : title},
				opposite: showRight
			}
		);

		return this;
	}

	public setColors(colors : string[]) : Chart {
		this.colors = colors;
		return this;
	}

	public setHeight(height : number) : Chart {
		this.height = height;
		return this;
	}

	public setLegend(legend : any) : Chart {
		this.legend = legend;
		return this;
	}
}
