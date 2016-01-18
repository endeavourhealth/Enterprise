/*global define, $*/

define([
'knockout'
    ],
    function (
ko
    ) {
        "use strict";

        function activate() {
            createAsNew();
        }

        function createAsNew() {
            vm.title("New Query");

            var rule = createNewRule();
            vm.rules.push(rule);
        }

        function createNewRule() {
            return {
                onTrue: "include",
                onFalse: "exclude"
            };
        }

        function requestAddNewRule() {

        }

        var vm = {
            activate: activate,
            title: ko.observable(),
            rules: ko.observableArray(),
            requestAddNewRule: requestAddNewRule
        };

        return vm;
    }
);