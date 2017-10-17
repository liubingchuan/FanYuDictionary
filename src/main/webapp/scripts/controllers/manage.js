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
            
            vm.pwdUser = '';
            vm.pwd = '';
            vm.pwdConfirm = pwdConfirm;
            vm.pwdUserConfirm = pwdUserConfirm;
            vm.pwdUserConfirmed = pwdUserConfirmed;
            vm.pwdDictionariesConfirmed = pwdDictionariesConfirmed;
            vm.isUsers = false;
            
            
            function pwdConfirm(pwd) {
            	  vm.pwd = pwd;
            }
            
            function pwdUserConfirm(pwdUser) {
          	  vm.pwdUser = pwdUser;
            }
            
            function pwdUserConfirmed() {
        	  /*UserService.deleteSingleUser(vm.userId).then(function (data) {
                  if (data === 'success') {
                	  $('#pwdConfirmModal').modal('hide');
                  }
                }
              );*/
        	  if (vm.pwdUser == passwordConfirm) {
        		  $('#pwdUserConfirmModal').modal('hide');
        		  //vm.isUsers = true;
        		  $state.go('manage.users');
        	  } else {
        		  alert('您的密码输入有误，请重新输入');
        	  }
            }
            
            function pwdDictionariesConfirmed() {
            	
            	 if (vm.pwd == passwordConfirm) {
		       		 $('#pwdDicConfirmModal').modal('hide');
		       		 $state.go('manage.managedictionary');
           	  	 } else {
           	  		 alert('您的密码输入有误，请重新输入');
           	  	 }
            	
            }
            
        });
})();
