/// <reference path="../../../typings/tsd.d.ts" />

angular.module('app.blocks.router',['ngRoute'])
    .run(['$route', function($route) {
        $route.reload();
    }]);