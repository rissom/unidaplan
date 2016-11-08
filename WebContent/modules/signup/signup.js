(function(){
'use strict';

function signupController(userService,$rootScope,$state,$translate,user,token){
	
	var thisController = this;
	this.fullname = "";
	this.username = "";
	this.email = "";
	
	this.user={};
	
	this.signup = function(){
		var userData = {
			id : user.id,
			token : token,
			fullname : this.fullname,
			username : this.username,
			email : this.email,
			password :  CryptoJS.SHA256(this.pwinput).toString(CryptoJS.enc.Base64),
		};
		userService.signUpUser(userData).then(
			function(data, status, headers, config){
				$rootScope.username = data.data.fullname;
				$rootScope.userid = data.data.id;
				if(data.data.admin){
					$rootScope.admin = data.data.admin;
				} else {
					$rootScope.admin = false;
				}
			    $rootScope.admin = ( data.data.admin == 'true' );			    
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