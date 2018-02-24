(function(){
  'use strict';

function editUserController($scope,$state,$stateParams,$translate,$uibModal,avProcessTypeService,avSampleTypeService,experiments,experimentService,ptypes,sampletypes,groups,user,userService){
  
	this.user = user;
	
	this.groups = groups;
	
	var tc = this;
	
	this.tValidTo = {value:user.tokenvalidto,editable:true};
	
	this.cancelEdit = function(){
		  tc.newFullname=user.fullname;
		  tc.newEmail=user.email;
		  tc.newUsername=user.username;
		  tc.editFullname=false;
		  tc.editEmail=false;
		  tc.editUsername=false;
	  }
	
	
	// initialize Editing fields
	this.cancelEdit();
	
	
	
	this.edit = function(field){
		tc.cancelEdit();
		switch (field){
			case "username" : tc.editUsername=true; break;
			case "fullname" : tc.editFullname=true; break;
			case "email" : tc.editEmail=true; break;
		}
		
	}
	
	
	
	this.getExperimentNr = function(experiment){
		for (var i=0; i<experiments.length; i++){
			if (experiments[i].id==experiment.id) {
				return experiments[i].number;
			}
		}
	}
	
	
	
	this.getExperimentName = function(experiment){
		for (var i=0; i<experiments.length; i++){
			if (experiments[i].id==experiment.id) {
				return experiments[i].namef();
			}
		}
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
	
	
	
	this.keyUp = function(keyCode,field) {
  	    if (keyCode===13) {				// Return key pressed
  	    	switch (field){
				case "username" : tc.updateUsername(); 	break;
				case "fullname" : tc.updateFullname();  break;
				case "email"    : tc.updateEmail(); 	break;
  	    	}
  			tc.cancelEdit();
	    }
	    if (keyCode===27) {		// Escape key pressed
			tc.cancelEdit();
	    }
	    
    };

    
    this.openGroupDialog = function () {
	    var heading = $translate.instant("ASSIGN USER TO GROUP",{user:tc.user.username});
	    var modalInstance = $uibModal.open({
		    animation: false,
		    templateUrl: 'modules/modal-user-group-choser/modal-group-choser.html',
		    controller: 'mGroupChoser as mGroupChoserCtrl',
		    size: 'lg',
		    resolve: {
		    	groups   : function() { return tc.groups || []; },
	    		buttonLabel : function() { return $translate.instant("Assign to user");},
		    	assignedGroups : function() { return tc.user.groups || [];},
	    		label	 : function() { return heading;}
		    }
		});
	    modalInstance.result.then(function(updatedGroups){
	    	if (updatedGroups.changed){
	    		var promise = userService.assignGroupToUser($stateParams.userID, updatedGroups.assignedGroups);
	    		promise.then(reload);
	    	}
	    })
	};
    
    
    this.openExperimentRightsDialog = function (user) {		
	    var h1= $translate.instant ("Assign rights for user");
	    var h2= $translate.instant ("to access experiments");
	    var modalInstance = $uibModal.open({
		    animation: false,
		    templateUrl: 'modules/modal-right-chosers/modal-experiment-right-choser.html',
		    controller: 'mExperimentRightChoser as mExperimentRightChoserCtrl',
		    size: 'lg',
		    resolve: {
		    	experiments   : function() { return experiments || []; },
	    		heading		  : function() { return h1+" '" +tc.user.fullname+"' "+h2;},
		    	assignedExperiments : function() { return tc.user.experiments || [];}
		    }
		});
	    modalInstance.result.then(function(updatedRights){
	    	if (updatedRights){
	    		var promise = experimentService.updateUserRights({userid:$stateParams.userID, updatedExRights: updatedRights});
	    		promise.then(reload);
	    	}
	    })
	};
	
    
  
    this.openProcessTypeRightsDialog = function (user) {		
	    var h1= $translate.instant ("Assign rights for user");
	    var h2= $translate.instant ("to access processtypes");
	    var modalInstance = $uibModal.open({
		    animation: false,
		    templateUrl: 'modules/modal-right-chosers/modal-processtype-right-choser.html',
		    controller: 'mProcesstypeRightChoser as mProcesstypeRightChoserCtrl',
		    size: 'lg',
		    resolve: {
		    	processtypes  : function() { return ptypes || []; },
	    		heading		  : function() { return h1+" '" +tc.user.fullname+"' "+h2;},
		    	assignedTypes : function() { return tc.user.processtypes || [];}
		    }
		});
	    modalInstance.result.then(function(updatedRights){
	    	if (updatedRights){
	    		var promise = avProcessTypeService.updateUserRights({userid:$stateParams.userID, updatedPTrights: updatedRights});
	    		promise.then(reload);
	    	}
	    })
	};
	
	
	
	this.openSampleTypeRightsDialog = function (group) {		
	    var h1= $translate.instant ("Assign rights for user");
	    var h2= $translate.instant ("to access sampletypes");
	    var modalInstance = $uibModal.open({
		    animation: false,
		    templateUrl: 'modules/modal-right-chosers/modal-sampletype-right-choser.html',
		    controller: 'mSampletypeRightChoser as mSampletypeRightChoserCtrl',
		    size: 'lg',
		    resolve: {
		        sampletypes	  : function() { return sampletypes || []; },
	    		heading		  : function() { return  h1+" '"+user.fullname+"' "+h2;},
	    		assignedTypes : function() { return tc.user.sampletypes || [];}
		    }
		});
	    modalInstance.result.then(function(updatedRights){
	    	if (updatedRights){
	    		var promise = avSampleTypeService.updateUserRights({userid:$stateParams.userID, updatedSTrights: updatedRights});
	    		promise.then(reload);
	    	}
	    })
	};
	
	
	this.pupdate = function(newTimestamp){
		var promise = userService.updateTokenValidTo($stateParams.userID,newTimestamp.date);
		promise.then(reload);
	}
	
	
	this.updateUsername = function (){ // Assign Users to a group
		var promise = userService.updateUsername($stateParams.userID,tc.newUsername);
		promise.then(reload);
	};
	
	
	
	this.updateEmail = function (){ // Assign Users to a group
		var promise = userService.updateEmail($stateParams.userID,tc.newEmail);
		promise.then(reload);
	};
	
	
	
	this.updateFullname = function (){ // Assign Users to a group
		var promise = userService.updateFullname($stateParams.userID,tc.newFullname);
		promise.then(reload)
	};
	
	
	
	var reload = function() {
		var current = $state.current;
		var params = angular.copy($stateParams);
		params.newParameter = false;
		return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};
  
};

angular.module('unidaplan').controller('editUserController', ['$scope','$state','$stateParams',
           '$translate','$uibModal','avProcessTypeService','avSampleTypeService','experiments','experimentService','ptypes','sampletypes',
           'groups','user','userService',editUserController]);

})();