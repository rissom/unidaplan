(function(){
'use strict';

function menuf($translate,$rootScope) {

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
			$rootScope.$broadcast('language changed',{'language':lang});
		}
	}
	
	
	this.getUserName = function(){
		return "Thorsten Rissom";
	}
	
	
	this.getLastLogin = function(){
		return "1.1.1901";
	}
	
}

angular.module('unidaplan').controller('menu',['$translate','$rootScope',menuf]);
  
})();