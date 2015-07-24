(function(){
'use strict';

var loginController=function($state,restfactory){
	

	this.userLogin = function(){
		console.log("yeah");
		var promise=restfactory.GET('login?user='+this.userinput+'&pw='+this.pwinput);
		promise.then(function(data){
				console.log("allesklar");
				if (restfactory.failedState) {
				  console.log("redirecting",restfactory.failedState);
				  $state.go(restfactory.failedState);
				}
			}, function(data){
				console.log("das war nix");
			}
		)
	}
}	
	


angular.module('unidaplan').controller('loginController',['$state','restfactory','$translate',loginController])
	
})();