'use strict';

angular.module('fitdonationsApp')
    .controller('RegisterController', function ($scope, $translate, $timeout, Auth, Principal) {
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.errorUserExists = null;
        $scope.registerAccount = {};
        $timeout(function (){angular.element('[ng-model="registerAccount.login"]').focus();});

        $scope.register = function () {
            if ($scope.registerAccount.password !== $scope.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
            } else {
                $scope.registerAccount.langKey = $translate.use();
                $scope.doNotMatch = null;
                $scope.error = null;
                $scope.errorUserExists = null;
                $scope.errorEmailExists = null;

                Auth.createAccount($scope.registerAccount).then(function () {
                    $scope.success = 'OK';
                }).catch(function (response) {
                    $scope.success = null;
                    if (response.status === 400 && response.data === 'login already in use') {
                        $scope.errorUserExists = 'ERROR';
                    } else if (response.status === 400 && response.data === 'e-mail address already in use') {
                        $scope.errorEmailExists = 'ERROR';
                    } else {
                        $scope.error = 'ERROR';
                    }
                });
            }
        };

        /* Initializers */
        // get client token
        Principal.clientToken().then(function(token) {
            braintree.setup(
                token,
                'dropin', {
                    container: "dropInContainer",
                    form: "registrationForm",
                    paymentMethodNonceReceived: function (event, nonce) {
                        $scope.registerAccount.paymentMethodNonce = nonce;
                        $scope.register();
                    }
                });
        });
    });
