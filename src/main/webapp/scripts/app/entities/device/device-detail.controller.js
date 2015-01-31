'use strict';

angular.module('fitdonationsApp')
    .controller('DeviceDetailController', function ($scope, $stateParams, Device, User) {
        $scope.device = {};
        $scope.load = function (id) {
            Device.get({id: id}, function(result) {
              $scope.device = result;
            });
        };
        $scope.load($stateParams.id);
    });
