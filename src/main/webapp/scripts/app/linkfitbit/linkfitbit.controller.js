'use strict';

angular.module('fitdonationsApp')
    .controller('LinkFitbitController', function ($scope, Principal) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
    });
