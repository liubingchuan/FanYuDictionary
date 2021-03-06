/**
 * @ngdoc function
 * @name fanYuFrontendApp.controller:ShowwordCtrl
 * @description
 * # ShowwordCtrl
 * Controller of the fanYuFrontendApp
 */
(function () {
    'use strict';
    angular.module('fanYuFrontendApp')
        .controller('ShowwordCtrl', function ($scope, $rootScope, $state, $http, AuthHttp, $stateParams, WordService) {
            var vm = this;
            vm.word = $stateParams.word;
            vm.wordDetail = [];
            vm.deleteWord = deleteWord;
            vm.publishWord = publishWord;

            /**** start add by cy 0826 ******/
            vm.wordId = '';
            /*vm.isDeleteAll = false;
            vm.isPublishAll = false;*/
            vm.deleteWordConfirm = deleteWordConfirm;
            //vm.deleteAllConfirm = deleteAllConfirm;
            vm.publishWordConfirm = publishWordConfirm;
            //vm.publishAllConfirm = publishAllConfirm;
            /**** end add by cy 0826 ******/

            getWordDetail();

            //通过事件订阅，当更新词条成功，则关闭编辑模式。
            $scope.$on('updateWordSuccess', updateWordSuccess);
            $rootScope.$on('updateDicSequenceSuccess', getWordDetail);

            function getWordDetail() {
                if ($.cookie('currentUser') == '' || $.cookie('currentUser') == undefined) {
                    WordService.getWordDetail(vm.word, 'N').then(function (data) {
                       vm.wordDetail = data;
                    });
                } else {
                    WordService.getWordDetail(vm.word, 'Y').then(function (data) {
                      /*var wordList = data;
                       for (var i in wordList) {
                       if (wordList[i].status == 'published') {
                       }
                       }*/
                      vm.wordDetail = data;
                    });
                }

            }

            /**** start add or edit by cy 0830 ******/
            function deleteWordConfirm(wordId) {
                vm.wordId = wordId;
                //vm.isDeleteAll = false;
            }

            function deleteWord(){
                WordService.deleteWord(vm.wordId).then(function(data){
                    if (data === 'success') {
                        $('#deleteWordConfirmModal').modal('hide');
                        //getWordDetail(vm.word);
                        getWordDetail();
                    }
                });
            }

            function publishWordConfirm(wordId) {
              vm.wordId = wordId;
              //vm.isPublishAll = false;
            }

            function publishWord(){
                WordService.publishWord(vm.wordId).then(function(data){
                    if (data === 'success') {
                        $('#pubulishWordConfirmModal').modal('hide');
                        //getWordDetail(vm.word);
                        getWordDetail();
                    }
                    else if (data === 'published') {
                    	alert('词条已经发布');
                    	$('#pubulishWordConfirmModal').modal('hide');
                        //getWordDetail(vm.word);
                    	getWordDetail();
                    }
                });
            }
            /**** end add or edit by cy 0830 ******/

            //更新词条成功，关闭编辑模式
            function updateWordSuccess(d,wordId){
                console.log(wordId);
                for (var i in vm.wordDetail) {
                    if (vm.wordDetail[i].id === wordId) {
                        vm.wordDetail[i].isEdit = false;
                    }
                }
                getWordDetail();
            }

        });
})();
