(function(){
'use strict';


function mGroupChoser($scope,$translate,$uibModalInstance,groups,assignedGroups,buttonLabel,label) {

	var tc=this;
	
	if (assignedGroups) {
		this.assignedGroups=assignedGroups.slice(0);
	} else {
		this.assignedGroups=[];
	}
	
	this.buttonLabel=buttonLabel;
	
	this.groups = groups;
	
	this.oldAssignedGroups=this.assignedGroups.slice(0);
	
		
	//activate function
	$scope.groupIsNotSelected=function(group){ // shows if a user is not already selected
		var found=false;
			angular.forEach(tc.assignedGroups, function(aGroup){
				if (aGroup.id==group.id){
					found=true
				}
			});
		return !found;
	}
	
	
	
	this.cancel=function(){
	    $uibModalInstance.close();
	}
	
	
	
	this.assignGroups=function(){    // pass the new list of users and if it changed
		var changed=!tc.oldAssignedGroups.equals(tc.assignedGroups) 
		var asGroups=[];
		tc.assignedGroups.map(function(g){asGroups.push(g.id)});
		$uibModalInstance.close({
	    	assignedGroups: asGroups,
	    	changed : changed}
	    );
	}
	
	
	
	this.getLabel = function(){
		return $translate.instant(label);
	}
	
	
	
	this.choseGroup = function(group){
		var i;
		var found=false; // check if the group is not already in assignedGroups
		for (i=0;i<tc.assignedGroups.length;i++){
			if (tc.assignedGroups[i].id==group.id){
				found=true;
			}
		}
		if (!found) {
			this.assignedGroups.push(group);
		}
	}

	
		
	this.removeGroup=function(group){
		for (var i=0;i<this.assignedGroups.length;i++){
			if (tc.assignedGroups[i].id==group.id){
				tc.assignedGroups.splice(i,1);
			}
		}
	}
	
	

};


angular.module('unidaplan').controller('mGroupChoser',['$scope','$translate','$uibModalInstance','groups',
		'assignedGroups','buttonLabel','label',mGroupChoser]);

})();