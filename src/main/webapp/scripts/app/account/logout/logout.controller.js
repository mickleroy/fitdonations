'use strict';

angular.module('fitdonationsApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
