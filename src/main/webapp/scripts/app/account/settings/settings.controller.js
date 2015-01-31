'use strict';

angular.module('fitdonationsApp')
    .controller('SettingsController', function ($scope, Principal, Auth, Device, $window) {
        $scope.success = null;
        $scope.error = null;
        Principal.identity().then(function(account) {
            $scope.settingsAccount = account;
        });

        $scope.save = function () {
            Auth.updateAccount($scope.settingsAccount).then(function() {
                $scope.error = null;
                $scope.success = 'OK';
                Principal.identity().then(function(account) {
                    $scope.settingsAccount = account;
                });
            }).catch(function() {
                $scope.success = null;
                $scope.error = 'ERROR';
            });
        };
        
        $scope.remove = function(deviceId) {
            Device.delete({id: deviceId});
        }
        
        $scope.linkDevice = function() {
            $window.location.href = '/api/device/link?userId=' + $scope.settingsAccount.id;
        }
    });
