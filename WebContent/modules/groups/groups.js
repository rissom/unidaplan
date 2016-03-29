(function(){
'use strict';

function groupController($uibModal,$translate,$scope,$state,$stateParams,groups,ptypes,sampletypes,users,userService) {
	
	this.groups = groups;
	
	this.users = users;

	this.strings = [];
	
	this.sampletypes = sampletypes;
	
	this.ptypes = ptypes;
	
	var thisController=this;
	
	
	
	this.isMemberOf = function(group){
		return function(user){
			var answer;
			if (group.members){
				answer = (group.members.indexOf(user.id)>-1);
			} else {
				answer = false;
			}
			return answer
		};
	}

	
	
	this.getProcesstypeName = function(pt){
		for (var i=0; i<ptypes.length; i++){
			if (ptypes[i].id==pt.id) {
				return ptypes[i].namef();
			}
		}
	}
	
	
	
	this.getSampletypeName = function(st){
		for (var i=0; i<sampletypes.length; i++){
			if (sampletypes[i].id==st.id) {
				return sampletypes[i].namef();
			}
		}
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
	
	
	
	this.openMembersDialog = function (group) {				
	    var modalInstanceM = $uibModal.open({
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
	    							return users.filter(function(testUser){return cUsers.indexOf(testUser.id)>-1});
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
	    
	  	modalInstanceM.result.then(function (result) {  // get the new Userlist + Info if it is changed from Modal. 
			if (result.changed==true){
				thisController.assignUsers(group,result.chosen);
			}
	    }, function () {
	        console.log('Strange Error: Modal dismissed at: ' + new Date());
	    });
	};

	
	
	this.openSampleTypeRightsDialog = function (group) {				
	    var modalInstance = $uibModal.open({
		    animation: false,
		    templateUrl: 'modules/modal-right-chosers/modal-sampletype-right-choser.html',
		    controller: 'mSampletypeRightChoser as mSampletypeRightChoserCtrl',
		    size: 'lg',
		    resolve: {
		        sampletypes	: function() { return sampletypes; }
		    }
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
    
        
angular.module('unidaplan').controller('groupController',['$uibModal','$translate','$scope','$state','$stateParams','groups','ptypes','sampletypes','users','userService',groupController]);

})();