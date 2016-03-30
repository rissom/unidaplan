(function(){
'use strict';


function mProcesstypeRightChoser($translate,$uibModalInstance,languages,restfactory,processtypes,group) {
	
	this.processtypes = processtypes || [];
	
	this.groupname = group.name;
	
	var groupPTypes = group.processtypes || [];
	
	for (var i=0; i<this.processtypes.length; i++){
		var found=false;
		for (var j=0; j<groupPTypes.length; j++){
			if (this.processtypes[i].id==groupPTypes[j].id){
				this.processtypes[i].permission=groupPTypes[j].permission;
				found=true;
			}
		}
		if (!found){this.processtypes[i].permission='l';}
	}

	var oldProcesstypes = angular.copy(this.processtypes);

	var thisController=this;		
	
	this.radioModel = 'Left';
	
	this.cancel=function(){ // Parameters where not changed
	    $uibModalInstance.close();
	};
	
	
	
	this.assignRights=function(){    // pass the list of changed rights
		var updatedRights = []; // new Array with changed rights
		for (var i=0; i<this.processtypes.length; i++){
			for (var j=0; j<oldProcesstypes.length; j++){
				if (this.processtypes[i].id==oldProcesstypes[j].id){
					if (this.processtypes[i].permission!=oldProcesstypes[j].permission){
						updatedRights.push({id:this.processtypes[i].id,permission:this.processtypes[i].permission})
					}
				}
			}
		}
	    $uibModalInstance.close({groupid:group.id, updatedPTrights: updatedRights});
	};
}

        
angular.module('unidaplan').controller('mProcesstypeRightChoser',['$translate','$uibModalInstance','languages',
                            'restfactory','processtypes','group',mProcesstypeRightChoser]);

})();