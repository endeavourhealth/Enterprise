/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />

module app.library {
	'use strict';

	class LibraryController {
		static $inject = ['LibraryService'];

		constructor(private libraryService:app.core.ILibraryService) {
		}

		getEngineHistory() {
			var vm = this;
			this.libraryService.getEngineHistory()
				.then(function (data) {
					// vm._engineHistoryData = data;
				});
		}
	}

	angular
		.module('app.library')
		.controller('LibraryController', LibraryController);
}
