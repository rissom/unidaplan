(function(){
'use strict';

var userService = function(restfactory,$q){

	var thisController=this;
	
	
	
	this.addGroup = function (group){
		return restfactory.POST("add-group");
	}
	
	
	this.assignGroupMembers = function (groupid,userList){ // Assign Users to a group
//		console.log ("chosen Users: ",userList);
		return restfactory.PUT("assign-group-members",{id:groupid,members:userList});
	}
	
	
	this.assignGroupToUser = function (userid,groups){ // Assign Users to a group
		return restfactory.PUT("assign-group-to-user",{userid:userid,groups:groups});
	}
	
	
	this.blockUser = function (user){ // Assign Users to a group
		return restfactory.PUT("update-user-data",{userid:user.id,blocked:true});
	}
	
	
	
	this.deleteGroup = function (group){
		return restfactory.DELETE("delete-group?id="+group.id);
	}
	
	
	
	// delete user
	this.deleteUser = function(user) {
		return restfactory.DELETE("delete-user?id="+user.id);
	};
	
	
	
	this.getGroups = function(){
		var defered=$q.defer();
	    var groups;
		var promise =  restfactory.GET("get-groups");
		promise.then(
			function(rest) {
				groups = rest.data;
				angular.forEach(groups,function(grp){
					grp.newName=grp.name;
				});
				defered.resolve(groups);
			},function(rest) {
			   	console.log("ERROR",rest);
			   	defered.reject(rest);
			}
			);
		return defered.promise;
	}
	
	
	
	// return multiple users
	this.getUsers = function() {
	    var defered=$q.defer();
	    var users;
		var promise = restfactory.GET("get-users");
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
	};
		
  
	
	// submit a new User return a promise of the new list of users 
	this.submitUser = function(newUser) {
        var defered=$q.defer();
        var users;
		var promise = restfactory.POST("add-user",newUser);
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
	};
	
	
	
	// sign up a new User (needs to have the correct token)
	this.signUpUser = function(userData){
		return restfactory.POST("sign-up",userData);
	};

	
	  
	// return a single user
	this.getUserWithToken = function(userID,token){
        var defered=$q.defer();
        var user;
		var promise = restfactory.GET("get-user?id="+userID+"&token="+token);
	    promise.then(
	    	function(rest) {
	    		if (rest.data.token==(token)){
	    			user = rest.data;
	    	    	defered.resolve(user);
	    		}else{
	    			thisController.error="wrong token";
	    	    	defered.reject("wrong token");
	    		} 
		    }, function(rest) {
		    	console.log("Error getting user");
		    	defered.reject("Error connecting to server");
		    }
	    );
		return defered.promise;
	};
	
	
	
	this.getUser = function(userID){
        var defered=$q.defer();
		var promise = restfactory.GET("get-user?id="+userID);
		promise.then(
	    	function(rest) {
	    		defered.resolve(rest.data);
		    }, function(rest) {
		    	console.log("Error getting user");
		    	defered.reject("Error connecting to server");
		    });
		return defered.promise;
	}
		
	
	
	// resend token
	this.resendToken = function(user) {
		return restfactory.PUT("resend-token?id="+user.id);
	};
	
	
	
	this.setLanguage = function(user, lang){
		return restfactory.PUT("update-user-data",{userid:user,preferredlanguage:lang});
	}
	
	
	
	this.updateUsername = function (userID,newUsername){ // Assign Users to a group
		return restfactory.PUT("update-user-data",{userid:userID,username:newUsername});
	}
	
	
	
	this.updateEmail = function (userID,newEmail){ // Assign Users to a group
		return restfactory.PUT("update-user-data",{userid:userID,email:newEmail});
	}
	
	
	
	this.updateFullname = function (userID,newFullname){ // Assign Users to a group
		return restfactory.PUT("update-user-data",{userid:userID,fullname:newFullname});
	}
	
	
	
	this.unblockUser = function (user){ // Assign Users to a group
		return restfactory.PUT("update-user-data",{userid:user.id,blocked:false});
	}
	
	
	
	this.updateGroupName = function (group){
		return restfactory.PUT("update-group-name",{id:group.id, name:group.newName})
	}
	
	
	
	this.updateTokenValidTo = function (userID,timestamp){
		return restfactory.PUT("update-user-data",{userid:userID,tokenvalidto:timestamp.toISOString()});
	}
};



angular.module('unidaplan').service('userService', ['restfactory','$q','$stateParams',userService]);

})();