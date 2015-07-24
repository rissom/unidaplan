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
			id : $stateParams.userID,
			token : $stateParams.token,
			fullname : this.fullname,
			username : this.username,
			email : this.email,
			password : this.pwinput,
		}
	var promise=restfactory.POST("sign-up",data);
	 promise.then(
			 function(data, status, headers, config){
				 $state.go("openExperiment");
			 },
			 function(data, status, headers, config){
			 }
	);
	 
	};

	
	
	// activate Function
	this.activate = function(){
		var promise = restfactory.GET("get-user.json?id="+$stateParams.userID+"&token="+$stateParams.token);
	    promise.then(
	    	function(rest) {
	    		if (rest.data.token==($stateParams.token)){
			    	thisController.fullname = rest.data.fullname;
			    	thisController.username = rest.data.username;
			    	thisController.email = rest.data.email;
	    		} else{
	    			thisController.error="wrong token";
	    		} 
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