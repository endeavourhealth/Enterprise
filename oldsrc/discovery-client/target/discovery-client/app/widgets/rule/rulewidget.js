/*global define*/

define(['knockout'],
    function (ko) {
        "use strict";

        return function () {

            function activate(data) {
                var rule = data.bindingContext.$data;

                vm.onTrue(rule.onTrue);
                vm.onFalse(rule.onFalse);
            }

            function detached() {
            }

            function requestNewTest() {

            }

            function requestExistingQuery() {

            }

            var vm = {
                activate: activate,
                detached: detached,

                onTrue: ko.observable(),
                onFalse: ko.observable(),

                requestNewTest: requestNewTest,
                requestExistingQuery: requestExistingQuery
            };

            return vm;
        };
    }
);