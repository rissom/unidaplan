(function(){
'use strict';

var loginController=function($state,restfactory){
	
	var thisController=this;
	
	this.error="";

	this.userLogin = function(){
		var promise=restfactory.GET('login?user='+this.userinput+'&pw='+this.pwinput);
		promise.then(function(data){
				console.log("logged in");
				thisController.error="";
				if (restfactory.failedState) {
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
}	
	


angular.module('unidaplan').controller('loginController',['$state','restfactory','$translate',loginController])
	
})();