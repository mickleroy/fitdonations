'use strict';

angular.module('fitdonationsApp')
    .controller('ChallengeDetailController', function ($scope, $stateParams, Challenge, User, Charity) {
        $scope.challenge = {};
        $scope.load = function (id) {
            Challenge.get({id: id}, function(result) {
              $scope.challenge = result;
            });
        };
        $scope.load($stateParams.id);
    });
