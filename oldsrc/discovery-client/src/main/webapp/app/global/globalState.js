/*global define, $*/

define(['knockout'],
    function (ko) {

        var vm = {
            queryEditor: {
                visible: ko.observable(),
                title: ko.observable()
            }
        };

        return vm;
    }
);