(function(){
'use strict';

function userController(users,userService,$state,$stateParams,$translate) {
	
	this.users = users;
	
	var tc = this;
	
	var addActions = function(){
		angular.forEach(tc.users,function(user){
			user.actions=[];
			user.actions.push({
				action : "edit",
				name   : function(){ return $translate.instant("edit"); }
			});
			if (user.blocked) {
				user.actions.push({
					action : "unblock",
					name   : function(){ return $translate.instant("unblock"); }
				});
			}else{
				user.actions.push({
					action : "block",
					name   : function(){ return $translate.instant("block"); }
				});
			}
			user.actions.push({
				action : "resendToken",
				name   : function(){ return $translate.instant("resend token") }
			});
			if (user.deletable){
				user.actions.push({
					action : "delete",
					name   : function(){return $translate.instant("delete"); }
				});
			}
		});
	}
	
	addActions();

	this.edit = false;
	
	
	
	this.blockUser = function (user){
		user.blocked = true;
		var promise = userService.blockUser(user);
		addActions();
	}
	
	
		
	var thisController = this;
	
	
	var reload = function() {
        	var current = $state.current;
        	var params = angular.copy($stateParams);
        	params.newSearch = false;
        	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};
	
	
	
	this.addUser = function() {
		this.edit = true;
	};
	
	
	
	this.cancel = function() {
		this.edit = false;
	};
	
	
	
	this.deleteUser = function(user) {
		var promise = userService.deleteUser(user);
	    promise.then(function(rest) {
	    	var promise2 = userService.getUsers();
	    	promise2.then(function(users){
	    		thisController.users=users;
	    		addActions();
	    		}, function() { 
	    		console.log("fehler")
	    	});
	    }, function() {
	    	console.log("ERROR");
	    });
	};
	


	this.performAction = function(action,user){
		switch (action.action){
			case "block" 	  : this.blockUser(user); break;
			case "edit" 	  : $state.go('editUser',{userID:user.id}); break;
			case "resendToken": this.resendToken(user); break;
			case "delete" 	  :	this.deleteUser(user); break;
			case "unblock" 	  : this.unblockUser(user); break;
		}
	};
	
	
	
	this.resendToken = function(user) {
		var promise = userService.resendToken(user);
	    promise.then(reload,
	        function(rest){ 
	    		    console.log("fehler");
	        }
	    );
	};

	

	this.submitUser = function() {
		var newUser =  { "fullname" : this.fullname,
						 "username" : this.username,
						    "email" : this.email};
		this.fullname = "";
		this.username = "";
		this.email = "";
		var promise = userService.submitUser(newUser);
		thisController.edit = false;
		promise.then(
			function(users){
				thisController.users = users;
				addActions();
			}, 
			function(users){
				console.log("fehler");
			}
		);
	};
	
	
	
	this.unblockUser = function (user){
		user.blocked = false;
		var promise = userService.unblockUser(user);
		addActions();
	}
	
}
  
        
angular.module('unidaplan').controller('userController',['users','userService','$state','$stateParams','$translate',userController]);

angular.module('unidaplan').directive('username', function() {
	return {
	    require: 'ngModel',
	    link: function(scope, elm, attrs, ctrl) {
	    	ctrl.$validators.integer = function(modelValue, viewValue) {
	    		if (ctrl.$isEmpty(modelValue)) {
	    			return true;
	    		}
	    		var valid=true;
	    		angular.forEach(scope.userCtrl.users, function(existinguser){
	    			if (existinguser.username == viewValue)
    				  	{
	        		      	valid=false;
    				  	}
	    		});
	    		return valid;
      	  	};
	    }
	};
});
	

})();