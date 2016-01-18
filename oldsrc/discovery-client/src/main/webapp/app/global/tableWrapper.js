/*global define, $*/

define([
        'datatables.net',
        'dataTablesSelectable'],
    function () {
        "use strict";

        var ctor = function (tableId, options) {

            var reference;

            function destroy() {

                options.data = null;

                if (reference) {
                    reference.destroy();
                    reference = null;
                }
            }

            function draw(data) {
                if (reference) {
                    reference.clear();

                    if (data)
                        reference.rows.add(data);

                    reference.draw();

                } else {
                    options.data = data;
                    var tableDom = $('#' + tableId);
                    reference = tableDom.DataTable(options);
                }
            }

            var vm = {
                destroy: destroy,
                draw: draw
            };

            return vm;
        };

        return ctor;
    }
);