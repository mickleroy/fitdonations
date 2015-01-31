'use strict';

angular.module('fitdonationsApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


