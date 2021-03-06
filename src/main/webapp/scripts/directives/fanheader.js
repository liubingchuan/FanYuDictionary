/**
 * @ngdoc directive
 * @name fanYuFrontendApp.directive:fanHeader
 * @description
 * # fanHeader
 */
(function () {
    'use strict';
    angular.module('fanYuFrontendApp')
        .directive('fanHeader', fanHeader);

    fanHeader.$inject = ['$rootScope', 'DictionaryService', 'UserService'];

    function fanHeader($rootScope, DictionaryService, UserService) {
        return {
            templateUrl: 'scripts/directives/templates/fanHeader.html',
            restrict: 'E',
            link: function postLink(scope, element, attrs) {
                scope.updateUserDicCheckList = updateUserDicCheckList;
                scope.logout = logout;
                scope.editUser = editUser;
                scope.sortableFanOptions = {
                    stop: function (e, ui) {
                        for (var index in $rootScope.fan_dictionaryList) {
                            $rootScope.fan_dictionaryList[index].dicSequence = index;
                        }
                        updateUserDicSequence();
                    }
                };
                scope.sortableBaOptions = {
                    stop: function (e, ui) {
                        for (var index in $rootScope.ba_dictionaryList) {
                            $rootScope.ba_dictionaryList[index].dicSequence = index;
                        }
                        updateUserDicSequence();
                    }
                };
                scope.sortableZangOptions = {
                    stop: function (e, ui) {
                        for (var index in $rootScope.zang_dictionaryList) {
                            $rootScope.zang_dictionaryList[index].dicSequence = index;
                        }
                        updateUserDicSequence();
                    }
                };
                scope.sortableHanOptions = {
                    stop: function (e, ui) {
                        for (var index in $rootScope.han_dictionaryList) {
                            $rootScope.han_dictionaryList[index].dicSequence = index;
                        }
                        updateUserDicSequence();
                    }
                };

                scope.changeIcon = function () {
                    scope.isBottomIcon = !scope.isBottomIcon;
                }

                scope.updateUserFromHeader = function () {
                    //vm.user = $rootScope.currentUser;
                    UserService.checkPassword($rootScope.currentUser.username, scope.oldPassword).then(function (response) {
                      var data = response.data;
                      console.log('status+++++++++++++++++'+ data);

                      //check all the element in this 回调 function, 因为此回调函数始终在 alert密码不正确 后点击ok 再执行，那么在check密码时不对。因此在回调函数里check 所有element....
                      if (scope.oldPassword == null || scope.oldPassword == '') {
                        alert('旧密码不能为空');
                      } else if (data == 'error') {
                        alert('您输入的密码不正確');
                      } else if ($rootScope.currentUser.password == null || $rootScope.currentUser.password == '') {
                        alert('密码不能为空');
                      } else if (scope.passwordConfirm == null || scope.passwordConfirm == '') {
                        alert('确认密码不能为空');
                      } else {
                        UserService.updateUser($rootScope.currentUser).then(function (data) {
                            console.log(data);
                            $('#updateCurrentUser').modal('hide');
                            //location.reload();
                            $.cookie("currentUser",JSON.stringify(data));
                          }
                        );
                        scope.oldPassword = '';
                        scope.passwordConfirm = '';
                      }
                    });
                };
                
                if ($.cookie('currentUser') == '' || $.cookie('currentUser') == undefined) {
                	DictionaryService.getDictionaryList('N').then(function(){
                        initUserDicSequence();
                        initUserDicCheckList();
                    });
                } else {
                	DictionaryService.getDictionaryList('Y').then(function(){
                        initUserDicSequence();
                        initUserDicCheckList();
                    });
                }
                
            }
        };

        function logout(){
            $rootScope.currentUser = {};
            $.removeCookie("currentUser");
            $.removeCookie("token");
            location.reload();
        }

        function editUser(){
            $rootScope.currentUser.password = '';
            //$.removeCookie("currentUser");
        }

        function initUserDicSequence(){
            //如果response里面没有dicSequence先初始化
            if ($rootScope.currentUser == undefined) {
                $rootScope.currentUser = {};
            }
            if ($rootScope.currentUser.dicSequence == undefined) {
                $rootScope.currentUser.dicSequence = {};
            }
            if ($rootScope.currentUser.dicSequence.sequence == undefined) {
                $rootScope.currentUser.dicSequence.sequence = {};
                //初始化四个词典组的次序。
                for (var index in $rootScope.fan_dictionaryList) {
                    $rootScope.fan_dictionaryList[index].dicSequence = index;
                    $rootScope.currentUser.dicSequence.sequence[$rootScope.fan_dictionaryList[index].id] = $rootScope.fan_dictionaryList[index].dicSequence;
                }
                for (var index in $rootScope.ba_dictionaryList) {
                    $rootScope.ba_dictionaryList[index].dicSequence = index;
                    $rootScope.currentUser.dicSequence.sequence[$rootScope.ba_dictionaryList[index].id] = $rootScope.ba_dictionaryList[index].dicSequence;
                }
                for (var index in $rootScope.zang_dictionaryList) {
                    $rootScope.zang_dictionaryList[index].dicSequence = index;
                    $rootScope.currentUser.dicSequence.sequence[$rootScope.zang_dictionaryList[index].id] = $rootScope.zang_dictionaryList[index].dicSequence;
                }
                for (var index in $rootScope.han_dictionaryList) {
                    $rootScope.han_dictionaryList[index].dicSequence = index;
                    $rootScope.currentUser.dicSequence.sequence[$rootScope.han_dictionaryList[index].id] = $rootScope.han_dictionaryList[index].dicSequence;
                }
            } else {
                //初始化四个词典组的次序。
                for (var index in $rootScope.fan_dictionaryList) {
                    $rootScope.fan_dictionaryList[index].dicSequence =  $rootScope.currentUser.dicSequence.sequence[$rootScope.fan_dictionaryList[index].id];
                }
                for (var index in $rootScope.ba_dictionaryList) {
                    $rootScope.ba_dictionaryList[index].dicSequence = $rootScope.currentUser.dicSequence.sequence[$rootScope.ba_dictionaryList[index].id];
                }
                for (var index in $rootScope.zang_dictionaryList) {
                    $rootScope.zang_dictionaryList[index].dicSequence =  $rootScope.currentUser.dicSequence.sequence[$rootScope.zang_dictionaryList[index].id];
                }
                for (var index in $rootScope.han_dictionaryList) {
                    $rootScope.han_dictionaryList[index].dicSequence = $rootScope.currentUser.dicSequence.sequence[$rootScope.han_dictionaryList[index].id];
                }
            }

        }

        function updateUserDicSequence() {
            var sequence = {};
            //Ugly code!
            for (var i in $rootScope.fan_dictionaryList) {
                sequence[$rootScope.fan_dictionaryList[i].id] = $rootScope.fan_dictionaryList[i].dicSequence;
            }
            for (var i in $rootScope.ba_dictionaryList) {
                sequence[$rootScope.ba_dictionaryList[i].id] = $rootScope.ba_dictionaryList[i].dicSequence;
            }
            for (var i in $rootScope.zang_dictionaryList) {
                sequence[$rootScope.zang_dictionaryList[i].id] = $rootScope.zang_dictionaryList[i].dicSequence;
            }
            for (var i in $rootScope.han_dictionaryList) {
                sequence[$rootScope.han_dictionaryList[i].id] = $rootScope.han_dictionaryList[i].dicSequence;
            }

            $rootScope.currentUser.dicSequence.sequence = sequence;

            //如果userid为空则说明是路人，不进行user update操作。
            if($rootScope.currentUser.id != undefined) {
                UserService.updateUserForFanHeader($rootScope.currentUser).then(function (data) {
                    $.cookie('currentUser', JSON.stringify($rootScope.currentUser));
                    $rootScope.$emit('updateDicSequenceSuccess', '');
                });
            }

        }

        function initUserDicCheckList(){
            //如果response里面没有dicSequence先初始化
            if ($rootScope.currentUser == undefined) {
                $rootScope.currentUser = {};
            }
            if ($rootScope.currentUser.dicSequence == undefined) {
                $rootScope.currentUser.dicSequence = {};
            }
            if ($rootScope.currentUser.dicSequence.checkList == undefined) {
                $rootScope.currentUser.dicSequence.checkList = [];
                //默认梵语字典全选
                for (var i in $rootScope.fan_dictionaryList) {
                    addCheckItem($rootScope.fan_dictionaryList[i].id);
                }
            }
        }

        function updateUserDicCheckList(event, dictionaryId) {
            //选中则添加到checklist
            if (event.target.checked) {
                removeOtherDicGroup(dictionaryId);
                addCheckItem(dictionaryId);
            } else {
                removeCheckItem(dictionaryId);
            }

            //如果userid为空则说明是路人，不进行user update操作。
            if($rootScope.currentUser.id != undefined){
                UserService.updateUserForFanHeader($rootScope.currentUser).then(function (data) {
                    $.cookie('currentUser', JSON.stringify($rootScope.currentUser));
                });
            }
        }

        function returnDictionaryGroupId(dictionaryId) {
			var isId = 0;
            for (var i1 in $rootScope.fan_dictionaryList) {
               if ($rootScope.fan_dictionaryList[i1].id == dictionaryId) {
                  isId = 1;
               }
            }
            for (var i2 in $rootScope.ba_dictionaryList) {
              if ($rootScope.ba_dictionaryList[i2].id == dictionaryId) {
                isId = 2;
              }
            }
            for (var i3 in $rootScope.zang_dictionaryList) {
              if ($rootScope.zang_dictionaryList[i3].id == dictionaryId) {
                isId = 3;
              }
            }
            for (var i4 in $rootScope.han_dictionaryList) {
              if ($rootScope.han_dictionaryList[i4].id == dictionaryId) {
                isId = 4;
              }
            }
			return isId;
		}
		
        function removeOtherDicGroup(dictionaryId) {
			var newSelectedDictionaryGroupId = returnDictionaryGroupId(dictionaryId);
			if($rootScope.currentUser.dicSequence.checkList.length>0) {
				var existingSelectedDictionaryGroupId = returnDictionaryGroupId($rootScope.currentUser.dicSequence.checkList[0]);
			} else {
				var existingSelectedDictionaryGroupId = 0;
			}
			if(newSelectedDictionaryGroupId != existingSelectedDictionaryGroupId) {
				$rootScope.currentUser.dicSequence.checkList=[];
			}		
        }
/*        function removeOtherDicGroup(dictionaryId) {
        	$rootScope.currentUser.dicSequence.checkList=[];
            var isId = 0;
            for (var i1 in $rootScope.fan_dictionaryList) {
               if ($rootScope.fan_dictionaryList[i1].id == dictionaryId) {
                  isId = 1;
               }
            }
            for (var i2 in $rootScope.ba_dictionaryList) {
              if ($rootScope.ba_dictionaryList[i2].id == dictionaryId) {
                isId = 2;
              }
            }
            for (var i3 in $rootScope.zang_dictionaryList) {
              if ($rootScope.zang_dictionaryList[i3].id == dictionaryId) {
                isId = 3;
              }
            }
            for (var i4 in $rootScope.han_dictionaryList) {
              if ($rootScope.han_dictionaryList[i4].id == dictionaryId) {
                isId = 4;
              }
            }

            if (isId == 1) {
                for (var i11 in $rootScope.ba_dictionaryList) {
                    removeCheckItem($rootScope.ba_dictionaryList[i11].id);
                }
                for (var i12 in $rootScope.zang_dictionaryList) {
                  removeCheckItem($rootScope.zang_dictionaryList[i12].id);
                }
                for (var i13 in $rootScope.han_dictionaryList) {
                  removeCheckItem($rootScope.han_dictionaryList[i13].id);
                }
            } else if (isId == 2) {
                for (var i21 in $rootScope.fan_dictionaryList) {
                  removeCheckItem($rootScope.fan_dictionaryList[i21].id);
                }
                for (var i22 in $rootScope.zang_dictionaryList) {
                  removeCheckItem($rootScope.zang_dictionaryList[i22].id);
                }
                for (var i23 in $rootScope.han_dictionaryList) {
                  removeCheckItem($rootScope.han_dictionaryList[i23].id);
                }
            } else if (isId == 3) {
              for (var i31 in $rootScope.fan_dictionaryList) {
                removeCheckItem($rootScope.fan_dictionaryList[i31].id);
              }
              for (var i32 in $rootScope.ba_dictionaryList) {
                removeCheckItem($rootScope.ba_dictionaryList[i32].id);
              }
              for (var i33 in $rootScope.han_dictionaryList) {
                removeCheckItem($rootScope.han_dictionaryList[i33].id);
              }
            } else if (isId == 4) {
              for (var i41 in $rootScope.fan_dictionaryList) {
                removeCheckItem($rootScope.fan_dictionaryList[i41].id);
              }
              for (var i42 in $rootScope.ba_dictionaryList) {
                removeCheckItem($rootScope.ba_dictionaryList[i42].id);
              }
              for (var i43 in $rootScope.zang_dictionaryList) {
                removeCheckItem($rootScope.zang_dictionaryList[i43].id);
              }
            }
        }*/

        function addCheckItem(dictionaryId){
            if ( $rootScope.currentUser.dicSequence.checkList.indexOf(dictionaryId) == -1) {
                $rootScope.currentUser.dicSequence.checkList.push(dictionaryId);
            }
        }

        function removeCheckItem(dictionaryId){
            for (var i in $rootScope.currentUser.dicSequence.checkList) {
                if ($rootScope.currentUser.dicSequence.checkList[i] == dictionaryId) {
                    //splice 删除数组中第i个元素
                    $rootScope.currentUser.dicSequence.checkList.splice(i,1);
                    break;
                }
            }
        }

    };


})();
