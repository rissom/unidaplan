(function(){
'use strict';

var loginController=function($state,restfactory,$scope,$rootScope,$translate){
	
	var thisController=this;
	
	this.error="";
	
	
	
	this.userLogin = function(){
		// is called when the user logs in.
		var promise=restfactory.GET('login?user='+this.userinput+'&pw='+CryptoJS.SHA256(this.pwinput).toString(CryptoJS.enc.Base64));
		promise.then(function(data){
				thisController.error="";
				$rootScope.username=data.data.fullname;
				$rootScope.userid=data.data.id;
				if(data.data.admin){
					$rootScope.admin=data.data.admin;
				} else {
					$rootScope.admin=false;
				}
				
				//language and username is stored in Browser storage.
		
			    window.localStorage.setItem("username",data.data.fullname);
			    if (data.data.admin){
			    	window.localStorage.setItem("admin",true);
			    	$rootScope.admin=true;
			    }else{
			    	window.localStorage.setItem("admin",false);
			    	$rootScope.admin=false;
			    }
			    window.localStorage.setItem("userid",data.data.id);
			    
				window.localStorage.setItem("language",data.data.preferredlanguage);

		        if(data.data.preferredlanguage && data.data.preferredlanguage !== null){
		        	  if (data.data.preferredlanguage!=$translate.use()) {
		      			$translate.use(data.data.preferredlanguage);
		        	  }
		        } 
				
				// did you want to go somewhere special? If not: sample chooser.
				if ($rootScope.failedState) {
					$state.transitionTo($rootScope.failedState,$rootScope.failedParams,{ reload: true, inherit: true, notify: true });
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