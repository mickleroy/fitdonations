'use strict';

angular.module('fitdonationsApp')
    .controller('DeviceController', function ($scope, Device, User) {
        $scope.devices = [];
        $scope.users = User.query();
        $scope.loadAll = function() {
            Device.query(function(result) {
               $scope.devices = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Device.save($scope.device,
                function () {
                    $scope.loadAll();
                    $('#saveDeviceModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.device = Device.get({id: id});
            $('#saveDeviceModal').modal('show');
        };

        $scope.delete = function (id) {
            $scope.device = Device.get({id: id});
            $('#deleteDeviceConfirmation').modal('show');
        };

        $scope.confirmDelete = function (id) {
            Device.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteDeviceConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.device = {accessToken: null, secretToken: null, id: null};
        };
    });
