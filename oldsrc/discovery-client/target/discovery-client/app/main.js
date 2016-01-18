/*global define*/

define(['durandal/system', 'durandal/app', 'durandal/viewLocator', 'plugins/widget'],

    function (system, app, viewLocator, widget) {
        system.debug(true);
        app.title = 'Discovery';

        app.configurePlugins({
            router: true,
            dialog: true,
            widget: true
        });

        app.start().then(function() {
            //Replace 'viewmodels' in the moduleId with 'views' to locate the view.
            //Look for partial views in a 'views' folder in the root.
            viewLocator.useConvention();

            //Show the app by setting the root view model for our application with a transition.
            app.setRoot('viewmodels/shell', 'entrance');

            widget.convertKindToModulePath = function (kind) {
                return "widgets/" + kind + "/" + kind + 'widget';
            };

            widget.convertKindToViewPath  = function (kind) {
                return "widgets/" + kind + "/" + kind + 'widget';
            };
        });
});