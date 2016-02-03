(function(){
'use strict';

function groupController($modal,$translate,$scope,$state,$stateParams,groups,users,userService) {
	
	this.groups = groups;
	
	this.users = users;

	this.strings = [];
	
	var thisController=this;
	
	
	
	this.isMemberOf = function(group){
		return function( user ) {
			return (group.members.indexOf(user.id)>-1)
		  };
	}
	
	

	this.addGroup = function(group) {
		var promise = userService.addGroup();
	    promise.then(function(rest) {
	    	reload();
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	}
	
	
	this.assignUsers = function(group,users){
		var userList=[];
		users.map(function(user){userList.push(user.id)});
		console.log("userList:"+userList)
//		console.log ("assigning Users: ",userList," to group: ", group.id);
		var promise = userService.assignGroupMembers(group.id,userList);
	    promise.then(function(rest) {
	    	reload();
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	}
	
	
	
	this.deleteGroup = function(group) {
		var promise = userService.deleteGroup(group);
	    promise.then(function(rest) {
	    	reload();
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	}
	
	
	
	this.keyUp = function(keyCode,group) {
		if (keyCode===13) {				// Return key pressed
			var promise=userService.updateGroupName(group);	
			promise.then(function(){
				reload();
			},function(){
				console.log("error");
			});
		}
		if (keyCode===27) {		// Escape key pressed
			group.newName=group.name;
			thisController.editmode=false;
		}
	};
	
	
	
	this.openDialog = function (group) {				
	    var modalInstance = $modal.open({
		    animation: false,
		    templateUrl: 'modules/modal-user-choser/modal-user-choser.html',
		    controller: 'modalUserChoser as mUserChoserCtrl',
		    size: 'lg',
		    resolve: {
		    	users 		: function() { return users; },
		    	chosenUsers : function() { 
		    						var cUsers=[]; 
		    						if (group.members){
		    							cUsers=group.members;
		    						}
	    							return users.filter(function(testUser){return group.members.indexOf(testUser.id)>-1});
		    				  },
		        groups      : function() { 
		        				var tgroups = groups.filter(function(testGroup){return testGroup!=group});
		        				tgroups.unshift({name:'<'+$translate.instant("all groups")+'>', id:0});
		        				return tgroups;
		        			  },
		        except		: function() {
//		        				var eSamples2=eSamples.slice(0);
//		        				eSamples2.push({sampleid:sample.id,typeid:sample.typeid,name:sample.name});
//		        				return eSamples2;
		        				return [];
		        				},
		        mode		: function() { return "multiple";},
		        buttonLabel	: function() { return $translate.instant('assign to group'); }
		    }		        
		});
	    
	  	modalInstance.result.then(function (result) {  // get the new Userlist + Info if it is changed from Modal. 
			if (result.changed==true){
				thisController.assignUsers(group,result.chosen);
			}
	    }, function () {
	        console.log('Strange Error: Modal dismissed at: ' + new Date());
	    });
	};

	
	
	this.refuse = function(group) { // is called when editing of groupname is cancelled
		group.newName=group.name;
		thisController.editmode=false;
	}
	
	

	var reload=function() { // reload this pages data
    	var current = $state.current;
    	var params = angular.copy($stateParams);
    	params.newSearch=false;
    	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };
	
};
    
        
angular.module('unidaplan').controller('groupController',['$modal','$translate','$scope','$state','$stateParams','groups','users','userService',groupController]);

})();