var webpack = require("webpack");
var HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
	entry: {
		'app' : './app/enterprise.app.ts',
		'vendor' : './app/vendor.ts'
	},
	output: {
		filename: './[name].bundle.js',
		path: '../webapp'
	},
	resolve: {
		extensions: ['', '.webpack.js', '.web.js', '.ts', '.js']
	},
	module: {
		loaders: [
			{ test: /\.ts$/, loader: 'awesome-typescript-loader' },
			{ test: /\.html/, loader: 'raw' },
			{ test: /\.css$/, loader: "style-loader!css-loader" },
			{ test: /\.less$/, loader: "style!css!less" },
			{	test: /\.(eot|svg|ttf|woff(2)?)(\?v=\d+\.\d+\.\d+)?/, loader: 'url' },
			{ test:  /\.(jpe?g|png|gif|svg)$/i, loader: 'file' }
		]
	},
	externals: {
		"jquery": "jQuery"
	},
	plugins: [
		new HtmlWebpackPlugin(
		{
			template: 'index.ejs',
			inject: 'body'
		}),
		new webpack.optimize.DedupePlugin()
	],
	devServer: {
		inline: true,
		contentBase: '..\\webapp',
		watch: true,
		progress: true,
		colors: true,

		proxy: {
			'/api': { target: 'http://localhost:8000' },
			'/public': { target: 'http://localhost:8000'}
		}
	}
};