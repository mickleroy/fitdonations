'use strict';

angular.module('fitdonationsApp')
    .controller('MainController', function ($scope, Principal, Challenge) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;

            if ($scope.isAuthenticated) {
                Challenge.progress(function(data) {
                    $scope.progress = data;
                });
            }
        });
    });
