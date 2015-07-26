(function(){
'use strict';

var loginController=function($state,restfactory,$scope){
	
	var thisController=this;
	
	this.error="";

	this.userLogin = function(){
		var promise=restfactory.GET('login?user='+this.userinput+'&pw='+this.pwinput);
		promise.then(function(data){
				console.log("logged in");
				thisController.error="";
				if (restfactory.failedState) {
				  console.log("failed state: ",restfactory.failedState.name);
				  if (restfactory.failedState.name!="login") {
					  $state.go(restfactory.failedState);
				  } else {
					  $state.go('openExperiment');
				  }
				} else {
					$state.go('openExperiment');	  
				}
			}, function(data){
				console.log("unknown user or wrong password");
				thisController.error="unknown user or wrong password";
			}
		)
	}
	
	
	this.keyUp = function(keyCode) {
		if (keyCode===13) {  	// Return key pressed
			this.userLogin();
		}
	}
}	
	


angular.module('unidaplan').controller('loginController',['$state','restfactory','$scope','$translate',loginController])
	
})();