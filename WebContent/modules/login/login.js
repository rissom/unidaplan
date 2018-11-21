(function(){
'use strict';

var loginController = function($state,restfactory,$scope,$rootScope,$translate){
	
	var thisController = this;
	
	this.error = "";
	
	
	
	this.userLogin = function(){
		// is called when the user logs in.
		var promise = restfactory.GET('login?user=' + this.userinput + '&pw='+CryptoJS.SHA256(this.pwinput).toString(CryptoJS.enc.Base64));
		promise.then(function(data){
				thisController.error = "";
				if (data.data.fullname){
					$rootScope.userfullname = data.data.fullname;
				} else {
					delete $rootScope.userfullname;
				}
				if (data.data.username){
					$rootScope.username = data.data.username;
				} else {
					delete $rootScope.username;
				}
				if (data.data.id>0){
					$rootScope.userid = data.data.id;
				} else {
					delete $rootScope.userid;
				}
						
				if(data.data.admin){
					$rootScope.admin = data.data.admin;
				} else {
					$rootScope.admin = false;
				}
				
			    if (data.data.admin){ 
			    	$rootScope.admin = true;
			    }else{
			    	$rootScope.admin = false;
			    }
			    

		        if(data.data.preferredlanguage && data.data.preferredlanguage !== null){
		        	  if (data.data.preferredlanguage != $translate.use()) {
		      			$translate.use(data.data.preferredlanguage);
		        	  }
		        } 
				
				// did you want to go somewhere special? If not: experiments.
				if ($rootScope.failedState) {
					$state.go($rootScope.failedState.name(),$rootScope.failedState.params());
				} else {
					$state.go('openExperiment');
				}
			}, function(data){
				thisController.error = $translate.instant("unknown user or wrong password");
			}
		)
	}
	
	
	this.keyUp = function(keyCode) {
		if (keyCode === 13) {  	// Log in user if Return key is pressed in pw-inputfield
			this.userLogin();
		}
	}
}	
	


angular.module('unidaplan').controller('loginController',['$state','restfactory','$scope','$rootScope','$translate',loginController])
	
})();