/*global define, $*/

define([],
    function () {

        function itemTypeForDisplay(input) {
            if (input === "QUERY")
                return "Query";
            else if (input === "CODESET")
                return "Code set";
            else
                return input;
        }

        var vm = {
            itemTypeForDisplay: itemTypeForDisplay
        };

        return vm;
    }
);