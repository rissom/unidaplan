(function(){
'use strict';

var loginController=function($state,restfactory,$scope,$translate){
	
	var thisController=this;
	
	this.error="";
	
	this.checkLocalStorageSupport =function() {
		  try {
		    return 'localStorage' in window && window.localStorage !== null;
		  } catch (e) {
		    return false;
		  }
		}

	
	
	this.userLogin = function(){
		var promise=restfactory.GET('login?user='+this.userinput+'&pw='+this.pwinput);
		promise.then(function(data){
				thisController.error="";
				if (restfactory.failedState) {
				  if (restfactory.failedState.name!="login") {
					  $state.go(restfactory.failedState);
				  } else {
					  if(thisController.checkLocalStorageSupport){
					        var lang = window.localStorage.getItem("language");
					        if(lang !== null){
					        	  if (this.old_language!=$translate.use()) {
					      			$translate.use(lang);
					        	  }
					        } 
					  }
					  $state.go('openExperiment');
				  }
				} else {
					$state.go('openExperiment');	  
				}
			}, function(data){
				thisController.error=$translate.instant("unknown user or wrong password");
			}
		)
	}
	
	
	this.keyUp = function(keyCode) {
		if (keyCode===13) {  	// Log in user if Return key is pressed in pw-inputfield
			this.userLogin();
		}
	}
}	
	


angular.module('unidaplan').controller('loginController',['$state','restfactory','$scope','$translate',loginController])
	
})();