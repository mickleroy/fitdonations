'use strict';

angular.module('fitdonationsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('challenge', {
                parent: 'entity',
                url: '/challenge',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/challenge/challenges.html',
                        controller: 'ChallengeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('challenge');
                        return $translate.refresh();
                    }]
                }
            })
            .state('challengeDetail', {
                parent: 'entity',
                url: '/challenge/:id',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/challenge/challenge-detail.html',
                        controller: 'ChallengeDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('challenge');
                        return $translate.refresh();
                    }]
                }
            });
    });
