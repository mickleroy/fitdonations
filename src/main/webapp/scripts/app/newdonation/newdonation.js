'use strict';

angular.module('fitdonationsApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('newdonation', {
                parent: 'site',
                url: 'newdonation',
                data: {
                    roles: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/newdonation/newdonation.html',
                        controller: 'NewDonationController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('newdonation');
                        return $translate.refresh();
                    }]
                }
            });
    });
