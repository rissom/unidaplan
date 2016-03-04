(function(){
'use strict';

var loginController=function($state,restfactory,$scope,$rootScope,$translate){
	
	var thisController=this;
	
	this.error="";
	
	
	
	this.userLogin = function(){
		// is called when the user logs in.
		var promise=restfactory.GET('login?user='+this.userinput+'&pw='+this.pwinput);
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
				var lang = window.localStorage.getItem("language");
		        if(lang !== null){
		        	  if (lang!=$translate.use()) {
		      			$translate.use(lang);
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