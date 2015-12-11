(function(){
'use strict';

function signupController(userService,$state,user,token){
	
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
				 $state.go("openExperiment");
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


angular.module('unidaplan').controller('signupController',['userService','$state','user','token',signupController]);
	
})();