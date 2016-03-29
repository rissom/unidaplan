(function(){
'use strict';


function modalUserGroupChoser($scope,$translate,$uibModalInstance,groups,users,chosenUsers,chosenGroups,except,buttonLabel,label) {

	var thisController=this;
	
	if (chosenUsers) {
		this.chosenUsers=chosenUsers.slice(0);
	} else {
		this.chosenUsers=[];
	}
	
	if (chosenGroups) {
		this.chosenGroups=chosenGroups.slice(0);
	} else {
		this.chosenGroups=[];
	}
	
	this.oldChosenUsers=this.chosenUsers.slice(0);
	
	this.oldChosenGroups=this.chosenGroups.slice(0);
	
	this.users=users;
	
	this.groups=groups;
	
				
		
	//activate function
	$scope.userIsNotSelected=function(user){ // shows if a user is not already selected
		var chosenUsers=thisController.chosenUsers;
		var found=false;
			angular.forEach(chosenUsers, function(cuser){
				if (cuser.id==user.id){
					found=true
				}
			});
		return !found;
	}
	
	
	$scope.groupIsNotSelected=function(group){ // shows if a group is not already selected
		var chosenGroups=thisController.chosenGroups;
		var found=false;
		angular.forEach(chosenGroups, function(cgroup){
			if (cgroup.id==group.id){
				found=true;
			}
		});
		if (group.id==1){
			found=true;
		}
		return !found;
	}
	
	
	this.groupSelected = function(user){ // is user in a selected group?
		return true;
	}
	
	
	
	this.cancel=function(){
	    $uibModalInstance.close({chosen:this.oldChosenUsers,changed:false});
	}
	
	
	
	this.grantRights=function(){    // pass the new list of users and if it changed
		var changed=!thisController.oldChosenUsers.equals(thisController.chosenUsers) ||
					!thisController.oldChosenGroups.equals(thisController.chosenGroups)
	    $uibModalInstance.close({
	    	chosenUsers: thisController.chosenUsers,
	    	chosenGroups: thisController.chosenGroups,
	    	changed : changed}
	    );
	}
	
	
	
	this.getButtonLabel = function(){
		return $translate.instant(buttonLabel);
	}
	
	
	
	this.getLabel = function(){
		return $translate.instant(label);
	}
	
	
	
	this.choseGroup = function(group){
		var i;
		var found=false; // check if the group is not already in chosenGroups
		for (i=0;i<this.chosenGroups.length;i++){
			if (this.chosenGroups[i].id==group.id){
				found=true;
			}
		}
		if (!found) {
			this.chosenGroups.push(group);
		}
	}

	
	
	this.choseUser=function(user){
		var i;
		var found=false; // check if the user is not already in chosenUsers
		for (i=0;i<this.chosenUsers.length;i++){
			if (this.chosenUsers[i].id==user.id){
				found=true;
			}
		}
		if (!found) {
			this.chosenUsers.push(user);
		}
	}
	
	
	
	this.removeGroup=function(group){
		for (var i=0;i<this.chosenGroups.length;i++){
			if (this.chosenGroups[i].id==group.id){
				this.chosenGroups.splice(i,1);
			}
		}
	}
	
	
	
	this.removeUser=function(user){ // remove User from list of chosen users.
		for (var i=0;i<this.chosenUsers.length;i++){
			if (this.chosenUsers[i].id==user.id){
				this.chosenUsers.splice(i,1);
			}
		}
	}
	
	
	

};


angular.module('unidaplan').controller('modalUserGroupChoser',['$scope','$translate','$uibModalInstance',
                            'groups','users','chosenUsers','chosenGroups','except','buttonLabel','label',modalUserGroupChoser]);

})();