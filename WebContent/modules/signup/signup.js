(function(){
'use strict';

function signupController(userService,$rootScope,$state,$translate,user,token){
	
	var thisController = this;
	thisController.fullname = user.fullname;
	thisController.username = user.username;
	thisController.email = user.email;
		
	this.signup = function(){
		//Send new Userdata + Password to server
		var userData = {
			id : user.id,
			token : token,
			fullname : thisController.fullname,
			username : thisController.username,
			email : thisController.email,
			password : CryptoJS.SHA256(this.pwinput).toString(CryptoJS.enc.Base64),
		};
		userService.signUpUser(userData).then(
			function(data){				
				$rootScope.userid = data.data.id;
				if (data.data.fullname){
					$rootScope.userfullname = data.data.fullname;
				}else{
					delete $rootScope.userfullname;
				}
				
				if (data.data.username){
					$rootScope.username = data.data.username;
				}else{
					delete $rootScope.username;
				}
				
				$rootScope.userid = data.data.id;
				if(data.data.admin){
					$rootScope.admin = data.data.admin;
				} else {
					$rootScope.admin = false;
				}
			    $rootScope.admin = ( data.data.admin == 'true' );			    
				var lang = data.data.language;
		        if(lang !== null){
		        	if (lang != $translate.use()) {
		        		$translate.use(lang);
		        	}
		        }
				$state.go("sampleChoser");
			},
			function(data){
				console.log("error signing in");
			}
		); 
	};
}


angular.module('unidaplan').controller('signupController',['userService','$rootScope','$state','$translate','user','token',signupController]);
	
})();