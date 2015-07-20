(function(){
'use strict';

function signupController(restfactory,$translate,$state,$scope,$stateParams){
	
	var thisController=this;
	this.username="";
	this.email="";
	
	this.user={};
	
	this.signup = function(){
		var data = {
			name : this.userinput,
			pw : this.pwinput,
			pw2 : this.pwinput2
		}
	console.log('Login in: '+data.name+' with pw: '+data.pw);	
	};

	
	
	// activate Function
	this.activate = function(){
		var promise = restfactory.GET("get-user.json?id="+$stateParams.userID);
		this.token =  $stateParams.token;
	    promise.then(
	    	function(rest) {
		    	thisController.username = rest.data.name;
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