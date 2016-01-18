define(['plugins/router', 'durandal/app', 'global/globalState', 'bootstrap'], function (router, app, globalState) {
    return {
        router: router,
        search: function() {
            //It's really easy to show a message box.
            //You can add custom options too. Also, it returns a promise for the user's response.
            app.showMessage('Search not yet implemented...');
        },
        activate: function () {
            router.map([
                { route: ['', 'dashboard'], title:'Dashboard', moduleId: 'viewmodels/dashboard', nav: true },
                { route: 'library', title:'Library', moduleId: 'viewmodels/library', nav: true },
                { route: 'reports', title:'Reports', moduleId: 'viewmodels/reports', nav: true },
                { route: 'patientRecord', title:'Patient Record', moduleId: 'viewmodels/patientRecord', nav: true },
                { route: 'administration', title:'Administration', moduleId: 'viewmodels/administration', nav: true },
                { route: 'audit', title:'Audit', moduleId: 'viewmodels/audit', nav: true },
                { route: 'queryEditor', title:'Query Editor', moduleId: 'viewmodels/queryEditor', nav: false }
            ]).buildNavigationModel();
            
            return router.activate();
        },
        globalState: globalState
    };
});