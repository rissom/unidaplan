(function(){
'use strict';


function mExperimentRightChoser($translate,$uibModalInstance,languages,restfactory,experiments,heading,assignedExperiments) {
		
	this.experiments = experiments || [];
	
	this.heading = heading
	
	for (var i=0; i<this.experiments.length; i++){
		var found=false;
		for (var j=0; j<assignedExperiments.length; j++){
			if (this.experiments[i].id==assignedExperiments[j].id){
				this.experiments[i].permission=assignedExperiments[j].permission;
				found=true;
			}
		}
		if (!found){this.experiments[i].permission='l';}
	}

	var oldExperiments = angular.copy(this.experiments);

	var thisController=this;		
	
	this.radioModel = 'Left';
	
	this.cancel=function(){ // Parameters where not changed
	    $uibModalInstance.close();
	};
	
	
	
	this.assignRights=function(){    // pass the list of changed rights
		var updatedRights = []; // new Array with changed rights
		for (var i=0; i<this.experiments.length; i++){
			for (var j=0; j<oldExperiments.length; j++){
				if (this.experiments[i].id==oldExperiments[j].id){
					if (this.experiments[i].permission!=oldExperiments[j].permission){
						updatedRights.push({id:this.experiments[i].id,permission:this.experiments[i].permission})
					}
				}
			}
		}
	    $uibModalInstance.close(updatedRights);
	};
}

        
angular.module('unidaplan').controller('mExperimentRightChoser',['$translate','$uibModalInstance','languages',
                            'restfactory','experiments','heading','assignedExperiments',mExperimentRightChoser]);

})();