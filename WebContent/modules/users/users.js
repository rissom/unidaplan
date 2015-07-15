(function(){
'use strict';

function userController(restfactory,$translate,$scope) {
	
	this.users =  [];			

	this.strings = [];
	
	this.myName='Thorsten Rissom';
	
	this.roles=["Admin","Technician","Scientist"]
	
	this.setRole=function(role,user){
		user.role=role;
//		var promise = restfactory.GET("change-experiment-status?id="+experiment.id+"&status="+status)
//	    promise.then(function(rest) {
//	    	console.log("status changed")
//	    }, function(rest) {
//	    	console.log("ERROR");
//	    });
	}
	
	this.loadData = function() {
		var promise = restfactory.GET("get-users.json"),
		 	userCtrl=this;
		
		
	    promise.then(function(rest) {
	    	userCtrl.users = rest.data;
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	var thisUserCtrl = this;
	$scope.$on('language changed', function(event, args) {
		thisUserCtrl.translate(args.language);
	});
	
	this.deleteUser = function(user) {
		var promise = restfactory.GET("delete-user?id="+user.id);
	    promise.then(function(rest) {
	    	console.log("user deleted")
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	}
	
	
	this.getRole = function(user) {
		if (user.role==undefined){
			return "horst";		
		}else{
			return this.roles[user.role];
		}
	};
	
	
	
	this.translate = function(lang) {
		if (lang=='en') {
			this.roles=["Admin","Technician","Scientist"];
		}else{
			this.roles=["Administrator","Techniker","Wissenschaftler"];
		}
	};
	
	// Activate function:
	this.loadData();
};
    
        
angular.module('unidaplan').controller('userController',['restfactory','$translate','$scope',userController]);

})();