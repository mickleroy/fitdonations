'use strict';

angular.module('fitdonationsApp')
    .controller('ChallengeController', function ($scope, Challenge, User, Charity) {
        $scope.challenges = [];
        $scope.users = User.query();
        $scope.charitys = Charity.query();
        $scope.loadAll = function() {
            Challenge.query(function(result) {
               $scope.challenges = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Challenge.save($scope.challenge,
                function () {
                    $scope.loadAll();
                    $('#saveChallengeModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.challenge = Challenge.get({id: id});
            $('#saveChallengeModal').modal('show');
        };

        $scope.delete = function (id) {
            $scope.challenge = Challenge.get({id: id});
            $('#deleteChallengeConfirmation').modal('show');
        };

        $scope.confirmDelete = function (id) {
            Challenge.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteChallengeConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.challenge = {amount: null, distance: null, startDate: null, endDate: null, id: null};
        };
    });
