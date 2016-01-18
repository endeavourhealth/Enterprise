/*global define, requirejs*/

requirejs.config({
    paths: {
        'text': '../lib/require/text',
        'durandal':'../lib/durandal/js',
        'plugins' : '../lib/durandal/js/plugins',
        'transitions' : '../lib/durandal/js/transitions',
        'knockout': '../lib/knockout/knockout-3.3.0',
        'bootstrap': '../lib/bootstrap/js/bootstrap.min',
        'jquery': '../lib/jquery/jquery-1.11.3.min',
        'jstree': '../lib/jstree/jstree.min',
        'datatables.net': '../lib/datatables.1.10.10/js/jquery.dataTables.min',
        'dataTablesSelectable': '../lib/datatables.1.10.10/js/dataTables.select.min',
        'moment': '../lib/moment/moment.min'
    },
    shim: {
        'bootstrap': {
            deps: ['jquery'],
            exports: 'jQuery'
        },
        // dataTablesSelectable: { deps: ['dataTables'] }
    }
});
