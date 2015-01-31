'use strict';

angular.module('fitdonationsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('linkfitbit', {
                parent: 'site',
                url: 'linkfitbit',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/linkfitbit/linkfitbit.html',
                        controller: 'NewChallengeController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('linkfitbit');
                        return $translate.refresh();
                    }]
                }
            });
    });
