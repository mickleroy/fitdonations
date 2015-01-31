'use strict';

angular.module('fitdonationsApp')
    .controller('NewDonationController', function ($scope, Principal, Challenge) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;



            $scope.donate = function() {
                console.log("*****************\n submitting donation\n***************** ");
                Challenge.donate({}).$promise.then(function(result){
                    // success
                    console.log(result);
                });


            };

            /* Initializers */

            // get client token
            Principal.clientToken().then(function(token) {
                braintree.setup(
                    token,
                    'dropin', {
                        container: 'dropin'
                    });
            });
        });
    });
