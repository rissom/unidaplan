(function(){
'use strict';

function menuf(restfactory,$translate,$rootScope,$state) {

	var thisController=this;
	
	this.navCollapsed=true;
	
//	this.status = {
//	    isopen: false
//	};

	this.oldlanguage='en';  
	
	this.checkLocalStorageSupport =function() {
		  try {
		    return 'localStorage' in window && window['localStorage'] !== null;
		  } catch (e) {
		    return false;
		  }
		}
	
	this.language = function(){
		return $translate.use();
	}
	
	this.setLanguage = function(lang){  // change language and send a broadcast
		if (this.old_language!=$translate.use()) {
			$translate.use(lang);
			if (thisController.checkLocalStorageSupport()) {
				window.localStorage.setItem("language",lang);
			};
		}
	}
	
	$rootScope.$on('$stateChangeStart', 
			function(event, toState, toParams, fromState, fromParams){ 
				thisController.navCollapsed = true;
	})
	
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