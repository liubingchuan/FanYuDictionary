/**
 * @ngdoc function
 * @name fanYuFrontendApp.controller:ManageCtrl
 * @description
 * # ManageCtrl
 * Controller of the fanYuFrontendApp
 */
(function () {
    'use strict';
    angular.module('fanYuFrontendApp')
        .controller('ManageCtrl', function ($scope, $rootScope, $state) {
            $scope.awesomeThings = [
                'HTML5 Boilerplate',
                'AngularJS',
                'Karma'
            ];
            
            var vm = this;
            
            vm.pwd = '';
            vm.pwdConfirm = pwdConfirm;
            vm.pwdUserConfirmed = pwdUserConfirmed;
            vm.pwdDictionariesConfirmed = pwdDictionariesConfirmed;
            vm.isUsers = false;
            
            
            function pwdConfirm(pwd) {
            	  vm.pwd = pwd;
            }
              
            function pwdUserConfirmed() {
        	  /*UserService.deleteSingleUser(vm.userId).then(function (data) {
                  if (data === 'success') {
                	  $('#pwdConfirmModal').modal('hide');
                  }
                }
              );*/
        	  if (vm.pwd == passwordConfirm) {
        		  $('#pwdUserConfirmModal').modal('hide');
        		  //vm.isUsers = true;
        		  $state.go('manage.users');
        	  }
            }
            
            function pwdDictionariesConfirmed() {
            	
            	 if (vm.pwd == passwordConfirm) {
		       		 $('#pwdDicConfirmModal').modal('hide');
		       		 $state.go('manage.managedictionary');
           	  	 }
            	
            }
            
        });
})();
