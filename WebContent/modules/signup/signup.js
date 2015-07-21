(function(){
'use strict';

function signupController(restfactory,$translate,$state,$scope,$stateParams){
	
	var thisController=this;
	this.fullname="";
	this.username="";
	this.email="";
	
	this.user={};
	
	this.signup = function(){
		var data = {
			fullname : this.fullname,
			username : this.username,
			pw : this.pwinput,
		}
	console.log('Login in: '+data.fullname+', username: '+data.username+' with pw: '+data.pw);	
	};

	
	
	// activate Function
	this.activate = function(){
		var promise = restfactory.GET("get-user.json?id="+$stateParams.userID);
		this.token =  $stateParams.token;
	    promise.then(
	    	function(rest) {
		    	thisController.fullname = rest.data.fullname;
		    	thisController.username = rest.data.username;
		    	thisController.email = rest.data.email;		    	
		    }, function(rest) {
		    	console.log("ERROR");
		    }
	    );
	}
	
	this.activate(); // run the activate function

	
	
//	$scope.$watch('sampleChoserCtrl.selectedtypes', function (seltypes){

}


angular.module('unidaplan').controller('signupController',['restfactory','$translate','$state','$scope','$stateParams',signupController])
	
})();