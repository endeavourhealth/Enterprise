/// <reference path="../../typings/tsd.d.ts" />
/// <reference path="../core/library.service.ts" />
/// <reference path="../blocks/logger.service.ts" />


module app.query {
    'use strict';

    class QueryController {
        engineHistoryData:app.models.EngineHistoryItem[];

        static $inject = ['LibraryService', 'LoggerService'];

        constructor(private libraryService:app.core.ILibraryService, private logger:app.blocks.ILoggerService) {
            this.getEngineHistory();
            logger.success('Query builder constructed', 'QueryData', 'Query');
        }

        getEngineHistory() {
            var vm:QueryController = this;
            this.libraryService.getEngineHistory()
                .then(function (data:app.models.EngineHistoryItem[]) {
                    vm.engineHistoryData = data;
                });
        }

    }

    angular
        .module('app.query')
        .controller('QueryController', QueryController);
}
