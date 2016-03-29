(function(){
'use strict';


function modalUserChoser($scope,$translate,$uibModalInstance,groups,users,chosenUsers,except,buttonLabel,mode) {

	var thisController=this;
	
	if (chosenUsers) {
		this.chosenUsers=chosenUsers.slice(0);
	} else {
		this.chosenUsers=[];
	}
	
	this.oldChosenUsers=this.chosenUsers.slice(0);
	
	this.users=users;
	
	this.groups=groups;
	
	this.selectedGroups=[groups[0]];

	//activate function
	this.firsttime=true;
			
	this.immediate= (mode=='immediate');
	
		
	
	$scope.isNotSelected=function(user){ // shows if a user is not already selected
		var chosenUsers=thisController.chosenUsers;
		var found=false;
			angular.forEach(chosenUsers, function(cuser){
				if (cuser.id==user.id){
					found=true
				}
			});
		return !found;
	}
	
	
	
	this.groupSelected = function(user){ // is user in a selected group?
		return true;
	}
	
	
	
	this.cancel=function(){
	    $uibModalInstance.close({chosen:this.oldChosenUsers,changed:false});
	}
	
	
	
	this.assignUsers=function(){    // pass the new list of users and if it changed
		var assignedUsersChanged=!thisController.oldChosenUsers.equals(this.chosenUsers)
	    $uibModalInstance.close({chosen: this.chosenUsers, changed : assignedUsersChanged});
	}
	
	
	
	this.getButtonLabel = function(){
		return $translate.instant(buttonLabel);
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
		if (this.immediate){
			console.log ("immediate mode")
			this.assignUsers()} // If the dialog is for just chosing one user immediately
	}
	
	
	
	this.removeUser=function(user){ // remove User from list of chosen users.
		for (var i=0;i<this.chosenUsers.length;i++){
			if (this.chosenUsers[i].id==user.id){
				this.chosenUsers.splice(i,1);
			}
		}
	}
	
	
	

};


angular.module('unidaplan').controller('modalUserChoser',['$scope','$translate','$uibModalInstance',
                            'groups','users','chosenUsers','except','buttonLabel','mode',modalUserChoser]);

})();