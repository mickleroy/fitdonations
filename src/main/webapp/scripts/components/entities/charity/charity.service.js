'use strict';

angular.module('fitdonationsApp')
    .factory('Charity', function ($resource) {
        return $resource('api/charitys/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            }
        });
    });
