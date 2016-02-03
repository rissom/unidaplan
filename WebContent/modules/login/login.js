(function(){
'use strict';

var loginController=function($state,restfactory,$scope,$rootScope,$translate){
	
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
		// is called when the user logs in.
		var promise=restfactory.GET('login?user='+this.userinput+'&pw='+this.pwinput);
		promise.then(function(data){
				thisController.error="";
				$rootScope.username=data.data.fullname;
				$rootScope.admin=data.data.admin;
				
				//language and username is stored in Browser storage.
				if(thisController.checkLocalStorageSupport){
				    window.localStorage.setItem("username",data.data.fullname);
				    window.localStorage.setItem("admin",data.data.admin);
					var lang = window.localStorage.getItem("language");
				        if(lang !== null){
				        	  if (lang!=$translate.use()) {
				      			$translate.use(lang);
				        	  }
				        } 
				}
				
				// did you want to go somewhere special? If not: sample chooser.
				if (restfactory.failedState) {
					$state.go(restfactory.failedState);
				} else {
					$state.go('sampleChoser');
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
	


angular.module('unidaplan').controller('loginController',['$state','restfactory','$scope','$rootScope','$translate',loginController])
	
})();