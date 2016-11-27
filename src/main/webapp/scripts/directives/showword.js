/**
 * @ngdoc directive
 * @name fanYuFrontendApp.directive:showWord
 * @description
 * # showWord
 */
(function () {
    'use strict';
    angular.module('fanYuFrontendApp')
        .directive('showWord', showWord);

    function showWord() {
        var showWordDirectiveController = showWordDirectiveController;

        showWordDirectiveController.$inject = ['$scope', '$rootScope', '$sce'];

        function showWordDirectiveController($scope, $rootScope, $sce) {
            var vm = this;
            vm.word = $scope.word;
            vm.changeIcon = changeIcon;
            vm.changeIconBC = changeIconBC;
            vm.isBottomIcon = false;
            vm.isBottomIconBC = false;
            
            
            function changeIcon () {
                vm.isBottomIcon = !vm.isBottomIcon;
            }
            
            function changeIconBC () {
                vm.isBottomIconBC = !vm.isBottomIconBC;
            }        
            
        }
        

        return {
            templateUrl: 'scripts/directives/templates/showWord.html',
            restrict: 'E',
            scope: {word: '='},
            controller: showWordDirectiveController,
            controllerAs: 'vm'
        };
    }
})();