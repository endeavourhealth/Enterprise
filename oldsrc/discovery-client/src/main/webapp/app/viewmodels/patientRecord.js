/*global define*/

define([
        'knockout'
    ],
    function (
        ko
    ) {
        "use strict";

        var table = {
            id: 'resultTable',
            reference: undefined
        };

        var plot = {
            id: 'clinical-plot',
            reference: undefined
        };

        var data = undefined;

        function findPatientClick() {
            if (!vm.patientId())
                return;

            $.ajax({
                url: 'rest/patient/' + vm.patientId() + '/careRecord',
                type: 'GET',
                dataType: 'html'
            }).done(function(json) {
                var data = $.parseJSON(json);
                receivePatientResults(data);
            }).fail(function(error) {
                console.log(error);
            });
        }

        function receivePatientResults(dataParam) {
            data = dataParam;
            var entity = data.entities[0];
            var dataSet = entity.rows;
            var columns = buildColumns(entity.fields);
            var options = {
                pageLength: 100,
                order: [[ 1, "desc" ]]
            };

            drawTable(table, dataSet, columns, options);

            vm.showResults(entity.rows.length !== 0);
        }

        function drawTable(tableInfo, dataset, columns, optionsParam) {

            clearSelectedItem();

            if (tableInfo.reference) {
                tableInfo.reference.clear();

                if (dataset)
                    tableInfo.reference.rows.add(dataset);

                tableInfo.reference.draw();
            } else {

                var tableDom = $('#' + tableInfo.id);

                var options = {
                    data: dataset,
                    columns: columns
                };

                $.extend(options, optionsParam);

                tableInfo.reference = tableDom.DataTable(options);
            }
        }

        function buildColumns(columns) {
            var result = [];

            for (var i = 0; i < columns.length; i++) {
                var column = columns[i];
                var columnValue = {
                    title: column.name
                };
                result.push(columnValue);
            }

            return result;
        }

        function initialise() {
            var selector = '#' + table.id;

            $(selector).on('click', 'tbody tr', function () {

                if ( $(this).hasClass('selected') ) {
                    //$(this).removeClass('selected');
                }
                else {
                    $(selector + ' tr.selected').removeClass('selected');
                    $(this).addClass('selected');

                    var guid = $(this).children('td').first().text();
                    itemSelected(guid);
                }
            } );
        }

        function itemSelected(guid) {
            clearSelectedItem();
            var row = findRow(guid);

            if (!row)
                return;

            var code = row[2];

            if (!code)
                return;

            plotCodes(code);
        }

        function findRow(guid) {
            for (var i = 0; i < this.data.entities[0].rows.length; i++) {
                if (this.data.entities[0].rows[i][0] === guid)
                    return this.data.entities[0].rows[i];
            }

            return null;
        }

        function plotCodes(code) {

            var data = getDataToPlot(code);

            if (data[0].length === 0)
                return;

            vm.plotValue('Plot : ' + code);

            var options = getPlotOptions();
            renderPlot(data, options);
        }

        function getDataToPlot(code) {
            var data = [];

            for (var i = 0; i < this.data.entities[0].rows.length; i++) {
                var row = this.data.entities[0].rows[i];

                if (row[2] === code) {
                    var date = row[1];
                    var value = row[3];

                    if (value === null || value === undefined)
                        continue;

                    data.push([date, value]);
                }
            }

            return [data];
        }

        function getPlotOptions(options) {

            if (!options)
                options = {};

            var chartOptions = {
                axesDefaults: {
                    tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                    tickOptions: {
                        fontSize: '11px',
                        markSize: 6
                    }
                },
                seriesDefaults: {
                    //color: uiHelper.colours.defaultGraph,
                    lineWidth: 1.5,
                    rendererOptions: {
                        smooth: true,
                    },
                    markerOptions: {
                        show: true,
                        size: 4
                    },
                    pointLabels: { show: false }
                },
                highlighter: {
                    show: true,
                    sizeAdjust: 4,
                    tooltipOffset: 7
                },
                axes: {
                    xaxis: {
                        renderer: $.jqplot.DateAxisRenderer,
                        tickOptions: {
                            angle: -30,
                            showGridline: false,
                            formatString: options.xFormatString
                        }
                    },
                    yaxis: {
                        rendererOptions: {
                            drawBaseline: false
                        },
                        tickOptions: {
                            formatString: options.yTickFormat
                        },
                        min: options.yMin
                    }
                },
                grid: {
                    background: '#ffffff',
                    shadow: false,
                    drawBorder: false
                }
            };

            return chartOptions;
        }

        function renderPlot(data, options) {
            clearPlot();
            plot.reference = renderPlotFromParts(plot.id, data, options);
        }

        function clearPlot() {
            if (plot.reference)
                plot.reference.destroy();
        }

        function renderPlotFromParts(placeholderId, data, options) {
            $('#' + placeholderId).empty();

            if (data && data.length > 0) {
                return $.jqplot(placeholderId, data, options);
            }
        }

        function clearSelectedItem() {
            vm.plotValue(null);
            clearPlot();
        }

        var vm = {
            patientId: ko.observable(5343),
            findPatientClick: findPatientClick,
            initialise: initialise,
            plotValue: ko.observable(),
            showResults: ko.observable(false)
        };

        return vm;
    }
);