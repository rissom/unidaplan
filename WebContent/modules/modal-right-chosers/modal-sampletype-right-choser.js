(function(){
'use strict';


function mSampletypeRightChoser($translate,$uibModalInstance,languages,restfactory,sampletypes,group) {
	
	this.sampletypes=sampletypes || [];
	
	this.groupname = group.name;
	
	var groupSTypes = group.sampletypes || [];

	for (var i=0; i<this.sampletypes.length; i++){
		var found=false;
		for (var j=0; j<groupSTypes.length; j++){
			if (this.sampletypes[i].id==groupSTypes[j].id){
				this.sampletypes[i].permission=groupSTypes[j].permission;
				found=true;
			}
		}
		if (!found) {this.sampletypes[i].permission="l";}
	}

	var oldSampletypes = angular.copy(this.sampletypes);

	var thisController=this;		
	
	this.radioModel = 'Left';
	
	this.cancel=function(){ // Parameters where not changed
	    $uibModalInstance.close();
	};
	
	
	
	this.assignRights=function(){    // pass the list of changed rights
		var updatedRights = []; // new Array with changed rights
		for (var i=0; i<this.sampletypes.length; i++){
			for (var j=0; j<oldSampletypes.length; j++){
				if (this.sampletypes[i].id==oldSampletypes[j].id){
					if (this.sampletypes[i].permission!=oldSampletypes[j].permission){
						updatedRights.push({id:this.sampletypes[i].id,permission:this.sampletypes[i].permission})
					}
				}
			}
		}
	    $uibModalInstance.close({groupid:group.id, updatedSTrights: updatedRights});
	};
}

        
angular.module('unidaplan').controller('mSampletypeRightChoser',['$translate','$uibModalInstance','languages',
                            'restfactory','sampletypes','group',mSampletypeRightChoser]);

})();