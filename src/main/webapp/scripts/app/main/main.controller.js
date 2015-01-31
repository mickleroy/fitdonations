'use strict';

angular.module('fitdonationsApp')
    .controller('MainController', function ($scope, Principal, Challenge) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;

            if ($scope.isAuthenticated) {
                Challenge.progress(function(data) {
                    $scope.progress = data;
                    $scope.currentChallenge = data.length;

                    if ($scope.currentChallenge) {
                        setTimeout(function() {
                            var ctx = document.getElementById("myChart").getContext("2d");
                            var chartData = {
                                labels: [],
                                datasets: [
                                    {
                                        label: "Activity",
                                        fillColor: "rgba(220,220,220,0.2)",
                                        strokeColor: "rgba(220,220,220,1)",
                                        pointColor: "rgba(220,220,220,1)",
                                        pointStrokeColor: "#fff",
                                        pointHighlightFill: "#fff",
                                        pointHighlightStroke: "rgba(220,220,220,1)",
                                        data: []
                                    }
                                ]
                            };
                            for (var i = 0; i < data[0].metersInDays.length; i++) {
                                var m = data[0].metersInDays[i];
                                chartData.labels.push(m.date);
                                chartData.datasets[0].data.push(m.meters);
                            }

                            new Chart(ctx).Line(chartData);
                            Chart.defaults.global.responsive = true;
                        }, 500);
                    }
                });

                Challenge.finished(function(data) {
                    $scope.finished = data;
                });
            }
        });
    });
