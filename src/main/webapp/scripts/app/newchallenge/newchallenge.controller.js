'use strict';

angular.module('fitdonationsApp')
    .controller('NewChallengeController', function ($scope, Charity, Challenge) {
        $scope.startChallenge = function() {
            Challenge.newChallenge($scope.challenge, function() {
                window.alert('SUCCESS');
            });
            return false;
        };

        Charity.query(function(result) {
            $scope.charitys = result;
        });
    });
