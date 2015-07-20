(function(){
'use strict';

function userController(restfactory,$translate,$scope) {
	
	this.users =  [];		
	
	this.edit = false;
	
	this.actions = ['block','edit'];
	
	var thisController = this;

	this.performAction=function(index,user){
		if (index==1){
			user.edit=true
		}
		if (index==3){
			this.deleteUser(user);
		}
	}
	
	this.getActions = function(user){
		var actions=[];
		actions.push("edit");
		if (user.blocked) {
			actions.push("unblock");
		}else{
			actions.push("block");
		}
		actions.push("resend token");
		if (user.deletable){
			actions.push("delete");
		}
		return actions
	}
	
	this.loadData = function() {
		var promise = restfactory.GET("get-users.json");
		promise.then(function(rest) {
	    	thisController.users = rest.data;
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	

	this.deleteUser = function(user) {
		var promise = restfactory.GET("delete-user?id="+user.id);
	    promise.then(function(rest) {
	    	thisController.loadData();
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	}	
	
	
	this.addUser = function() {
		this.edit=true;
	}
	
	
	this.submitUser = function() {
		var newUser=  { "fullname" : this.fullname,
						"username" : this.username,
						   "email" : this.email}
		var promise = restfactory.POST("add-user.json",newUser);
		promise.then(function(data, status, headers, config){
				thisController.loadData();
				thisController.edit=false;
			},
					 function(data, status, headers, config){
				console.log('Error');
			});
	}
	
	
	
	
	// Activate function:
	this.loadData();
};
    
        
angular.module('unidaplan').controller('userController',['restfactory','$translate','$scope',userController]);

})();