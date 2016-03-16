/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.listOuput {
	import ILoggerService = app.blocks.ILoggerService;
	import IScope = angular.IScope;
	import ILibraryService = app.core.ILibraryService;
	'use strict';

	export class ListOuputController {
		static $inject = ['LibraryService', 'LoggerService', '$scope'];

		constructor(
			protected libraryService:ILibraryService,
			protected logger : ILoggerService,
			protected $scope : IScope) {
		}
	}

	angular
		.module('app.listOutput')
		.controller('ListOutputController', ListOuputController);
}
