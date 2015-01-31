'use strict';

angular.module('fitdonationsApp')
    .controller('CharityDetailController', function ($scope, $stateParams, Charity, Challenge) {
        $scope.charity = {};
        $scope.load = function (id) {
            Charity.get({id: id}, function(result) {
              $scope.charity = result;
            });
        };
        $scope.load($stateParams.id);
    });
