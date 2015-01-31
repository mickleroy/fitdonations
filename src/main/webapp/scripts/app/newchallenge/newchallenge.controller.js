'use strict';

angular.module('fitdonationsApp')
    .controller('NewChallengeController', function ($scope, Charity, Challenge, $state) {
        $scope.startChallenge = function() {
            Challenge.newChallenge($scope.challenge, function() {
                $state.go('home');
            });
            return false;
        };

        Charity.query(function(result) {
            $scope.charitys = result;
        });
    });
