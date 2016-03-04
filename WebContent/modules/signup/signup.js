(function(){
'use strict';

function signupController(userService,$rootScope,$state,$translate,user,token){
	
	var thisController=this;
	this.fullname="";
	this.username="";
	this.email="";
	
	this.user={};
	
	this.signup = function(){
		var userData = {
			id : user.id,
			token : token,
			fullname : this.fullname,
			username : this.username,
			email : this.email,
			password : this.pwinput,
		};
		userService.signUpUser(userData).then(
			function(data, status, headers, config){
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
				$state.go("sampleChoser");
			 },
			 function(data, status, headers, config){
			 }
			 
		); 
	};
	
	
	// activate Function
	thisController.fullname = user.fullname;
	thisController.username = user.username;
	thisController.email = user.email;

}


angular.module('unidaplan').controller('signupController',['userService','$rootScope','$state','$translate','user','token',signupController]);
	
})();