'use strict';

angular.module('fitdonationsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('newchallenge', {
                parent: 'site',
                url: 'newchallenge',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/newchallenge/newchallenge.html',
                        controller: 'NewChallengeController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('newchallenge');
                        return $translate.refresh();
                    }]
                }
            });
    });
