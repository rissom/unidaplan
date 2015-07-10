(function(){
'use strict';

function menuf($translate,$rootscope) {

	this.status = {
	    isopen: false
	};

	this.oldlanguage='en';  
	
	this.language = function(){
		return $translate.use();
	}
	
	this.setLanguage = function(lang){  // change language and send a broadcast
		if (this.old_language!=$translate.use()) {
			$translate.use(lang);
			$rootscope.$broadcast('language changed',{'language':lang});
		}
	}
}

angular.module('unidaplan').controller('menu',['$translate','$scope',menuf]);
  
})();