'use strict';

angular.module('fitdonationsApp')
    .factory('Challenge', function ($resource) {
        return $resource('api/challenges/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.startDate = new Date(data.startDate);
                    data.endDate = new Date(data.endDate);
                    return data;
                }
            }
        });
    });
