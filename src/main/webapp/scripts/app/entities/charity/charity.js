'use strict';

angular.module('fitdonationsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('charity', {
                parent: 'entity',
                url: '/charity',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/charity/charitys.html',
                        controller: 'CharityController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('charity');
                        return $translate.refresh();
                    }]
                }
            })
            .state('charityDetail', {
                parent: 'entity',
                url: '/charity/:id',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/charity/charity-detail.html',
                        controller: 'CharityDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('charity');
                        return $translate.refresh();
                    }]
                }
            });
    });
