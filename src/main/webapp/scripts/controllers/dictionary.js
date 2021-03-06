/**
 * @ngdoc function
 * @name fanYuFrontendApp.controller:DictionaryCtrl
 * @description
 * # DictionaryCtrl
 * Controller of the fanYuFrontendApp
 */
(function () {
    'use strict';
    angular.module('fanYuFrontendApp')
        .controller('DictionaryCtrl', DictionaryCtrl);
    DictionaryCtrl.$inject = ['$scope', '$rootScope', 'WordService'];
    function DictionaryCtrl($scope, $rootScope, WordService) {
        var vm = this;

        vm.searchResult = [];


        vm.typeSpecialChar = typeSpecialChar;
        vm.setSearchCode = setSearchCode;
        vm.setSearchMatch = setSearchMatch;
        vm.setSearchDomain = setSearchDomain;
        vm.searchWord = searchWord;

        vm.search = {};
        vm.search.searchWord = "";
        vm.search.code = "Unicode";
        vm.search.match = "shou";
        vm.search.domain = "danci";
        vm.search.dictionaries = "";


        vm.softKeys = ["ā", "ī", "ū", "ṛ", "ṝ", "ḷ", "ḹ", "ṃ", "ḥ", "ṅ", "ñ", "ṭ", "ḍ", "ṇ", "ś", "ṣ"];
        vm.codeList = [
            {
                codeName: "HK",
                codeValue: "HK",
                checked: false
            },
            {
                codeName: "Unicode",
                codeValue: "Unicode",
                checked: true
            },
            {
                codeName: "模糊",
                codeValue: "mohu",
                checked: false
            }
        ];

        vm.matchList = [
            {
                matchName: "首",
                matchValue: "shou",
                checked: true
            },
            {
                matchName: "尾",
                matchValue: "wei",
                checked: false
            },
            {
                matchName: "中",
                matchValue: "zhong",
                checked: false
            },
            {
                matchName: "精确",
                matchValue: "jingque",
                checked: false
            }
        ];

        vm.domainList = [
            {
                domainName: "单词",
                domainValue: "danci",
                checked: true
            },
            {
                domainName: "对译词",
                domainValue: "duiyingci",
                checked: false
            },
            {
                domainName: "变形",
                domainValue: "bianxing",
                checked: false
            },
            {
                domainName: "例句",
                domainValue: "liju",
                checked: false
            },
            {
                domainName: "全文",
                domainValue: "quanwen",
                checked: false
            }
        ];


        function typeSpecialChar(event) {
            vm.search.searchWord += $(event.target).text().trim();
            $("#searchInput").focus();
        }

        function setSearchCode(codeValue) {
            vm.search.code = codeValue;
        }

        function setSearchMatch(matchValue) {
        	vm.search.match = matchValue;
        }

        function setSearchDomain(domainValue) {
            vm.search.domain = domainValue;
            if (vm.search.domain !== 'danci' ) {
        		vm.search.match = 'zhong';
        		//$("input[name='match'][value='zhong']").attr("checked",true); 
        		$("input[name='match'][type='radio']").get(2).checked = true;
        		$("input[name='match'][type='radio']").get(0).disabled = true;
        		$("input[name='match'][type='radio']").get(1).disabled = true;
        		$("input[name='match'][type='radio']").get(3).disabled = true;
        	} else {
        		$("input[name='match'][type='radio']").get(0).disabled = false;
        		$("input[name='match'][type='radio']").get(1).disabled = false;
        		$("input[name='match'][type='radio']").get(3).disabled = false;
        	} 
            
        }

        function searchWord() {
        	vm.search.dictionaries = "";
            //设置当前用户查询那些字典（在header里面选中的那些字典）。
            if( $rootScope.currentUser.dicSequence != undefined){
                for(var i in $rootScope.currentUser.dicSequence.checkList) {
                	vm.search.dictionaries += $rootScope.currentUser.dicSequence.checkList[i]+'@';
                }
            }

            $('#collapseSoftKeys').collapse('hide');
            $('#collapseSearchSettings').collapse('hide');
            if (searchValidation()) {
                if ($.cookie('currentUser') == '' || $.cookie('currentUser') == undefined) {
                  WordService.searchWord(vm.search, 'N').then(function (data) {
                    vm.searchResult = data;
                  });
                } else {
                  WordService.searchWord(vm.search, 'Y').then(function (data) {
                    vm.searchResult = data;
                  });
                }
            }
        }

        function searchValidation() {
            if (vm.search.searchWord != undefined && vm.search.searchWord != "") {
                return true;
            } else {
                return false;
            }
        }
    }
})();
