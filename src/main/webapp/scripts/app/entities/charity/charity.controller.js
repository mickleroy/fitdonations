'use strict';

angular.module('fitdonationsApp')
    .controller('CharityController', function ($scope, Charity, Challenge) {
        $scope.charitys = [];
        $scope.challenges = Challenge.query();
        $scope.loadAll = function() {
            Charity.query(function(result) {
               $scope.charitys = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Charity.save($scope.charity,
                function () {
                    $scope.loadAll();
                    $('#saveCharityModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.charity = Charity.get({id: id});
            $('#saveCharityModal').modal('show');
        };

        $scope.delete = function (id) {
            $scope.charity = Charity.get({id: id});
            $('#deleteCharityConfirmation').modal('show');
        };

        $scope.confirmDelete = function (id) {
            Charity.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteCharityConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.charity = {name: null, id: null};
        };
    });
