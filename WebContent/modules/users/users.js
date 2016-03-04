(function(){
'use strict';

function userController(users,userService,$state,$stateParams,$translate) {
	
	this.users =  users;
	
	this.edit = false;
	
	this.actions = ['block','edit'];
	
	var thisController = this;
	
	
	var reload=function() {
    	var current = $state.current;
    	var params = angular.copy($stateParams);
    	params.newSearch=false;
    	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};
	
	

	this.performAction=function(index,user){
		if (index==1){
			user.edit=true;
		}
		if (index==2){
			this.resendToken(user);
		}
		if (index==3){
			this.deleteUser(user);
		}
	};
	
	
	
	this.getActions = function(user){
		var actions=[];
		actions.push($translate.instant("edit"));
		if (user.blocked) {
			actions.push($translate.instant("unblock"));
		}else{
			actions.push($translate.instant("block"));
		}
		actions.push($translate.instant("resend token"));
		if (user.deletable){
			actions.push($translate.instant("delete"));
		}
		return actions;
	};
	
	
	
	this.resendToken = function(user) {
		var promise = userService.resendToken(user);
	    promise.then(
	    	function(rest) {
	    		reload();
	    	},function(rest){ 
	    		console.log("fehler");
	    	}
	    );
	};

	
	
	this.deleteUser = function(user) {
		var promise = userService.deleteUser(user);
	    promise.then(function(rest) {
	    	var promise2 = userService.getUsers();
	    	promise2.then(function(users){
	    		thisController.users=users;
	    	}, console.log("fehler"));
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	
	
	
	this.addUser = function() {
		this.edit=true;
	};
	
	
	
	this.cancel = function() {
		this.edit=false;
	};
	
	
	
	this.submitUser = function() {
		var newUser=  { "fullname" : this.fullname,
						"username" : this.username,
						   "email" : this.email};
		this.fullname="";
		this.username="";
		this.email="";
		var promise = userService.submitUser(newUser);
		thisController.edit=false;
		promise.then(
			function(users){
				thisController.users=users;
			}, 
			function(users){
				console.log("fehler");
			}
		);
	};
	
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
	    			if (existinguser.username==viewValue)
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