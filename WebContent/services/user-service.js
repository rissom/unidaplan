(function(){
'use strict';

var userService = function(restfactory,$q,$stateParams){

	var thisController=this;
	
	// return multiple users
	this.getUsers = function() {
	    var defered=$q.defer();
	    var users;
		var promise = restfactory.GET("get-users.json");
		promise.then(
			function(rest) {
				users = rest.data;
				defered.resolve(users);
			},function(rest) {
			   	console.log("ERROR",rest);
			   	defered.reject(rest);
			}
		);
		return defered.promise;
	}
		
  
	
	// submit a new User return a promise of the new list of users 
	this.submitUser = function(newUser) {
        var defered=$q.defer();
        var users;
		var promise = restfactory.POST("add-user.json",newUser);
		promise.then(
			function(data){
				var promise2 = thisController.getUsers();
				promise2.then(function(users) {
					defered.resolve(users);
				}, function(error) {
					defered.reject(error);
				});
	    	},function(rest) {
			   	console.log("ERROR");
			   	defered.resolve(users);
			}
	    );
		return defered.promise;
	}
	
	
	
	  
	// return a single user
	this.getUser = function(token){
        var defered=$q.defer();
        var user;
		var promise = restfactory.GET("get-user.json?id="+$stateParams.userID+"&token="+token);
	    promise.then(
	    	function(rest) {
	    		if (rest.data.token==(token)){
			    	user.fullname = rest.data.fullname;
			    	user.username = rest.data.username;
			    	user.email = rest.data.email;
	    	    	defered.resolve(user);
	    		}else{
	    			thisController.error="wrong token";
	    	    	defered.resolve(Null);
	    		} 
		    }, function(rest) {
		    	console.log("Error getting user");
		    	defered.resolve(Null);
		    }
	    );
		return defered.promise;
	}
	
	
	
	// delete user
	this.deleteUser = function(user) {
		return restfactory.GET("delete-user?id="+user.id);
	}	
	
}



angular.module('unidaplan').service('userService', ['restfactory','$q','$stateParams',userService]);

})();