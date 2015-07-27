(function(){
'use strict';

function menuf(restfactory,$translate,$rootScope,$state) {

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
	
	this.logout = function(){
		var promise = restfactory.GET('logout');
		promise.then(function(){
				$state.go('login');
			},
			function(){
				console.log("Error!");	
			});
	}
	
	this.getUserName = function(){
		return "Thorsten Rissom";
	}
	
	
	this.getLastLogin = function(){
		return "1.1.1901";
	}
	
}

angular.module('unidaplan').controller('menu',['restfactory','$translate','$rootScope','$state',menuf]);
  
})();