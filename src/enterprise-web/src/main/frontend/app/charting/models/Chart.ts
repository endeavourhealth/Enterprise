import {Series} from "./Series";

export class Chart {
	title : string;
	categories : string[] = [];
	series : Series[] = [];
	yAxis : any[] = [];

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

	public export() {
		let header = 'Series,' + this.categories.join(',');

		let rows = this.series.map(
			series => series.name + ',' + series.data.join(',')
		).join('\n');

		let blob = new Blob([header, '\n', rows], { type: 'text/plain' });
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
}
