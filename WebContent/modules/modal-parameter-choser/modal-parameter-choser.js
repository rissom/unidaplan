(function(){
'use strict';


function modalParameterChoser($translate,$modalInstance,languages,restfactory,avParameters,chosenParameters) {

	
	this.avParameters=avParameters;
	this.selectedParameters=chosenParameters;

	var thisController=this;
	console.log("av Parameters:",avParameters)
	console.log("chosen Parameters:",chosenParameters)
	
	
	
	this.cancel=function(){ // Parameters where not changed
	    $modalInstance.close({chosen:this.chosenParameters,changed:false});
	}
	
	
	
	this.assignParameters=function(){    // pass the new list of parameters if it has changed
		var assignedParametersChanged=!chosenParameters.equals(this.selectedParameters)
	    $modalInstance.close({chosen: this.selectedParameters, changed : assignedParametersChanged});
	}
};

        
angular.module('unidaplan').controller('modalParameterChoser',['$translate','$modalInstance','languages',
                            'restfactory','avParameters','chosenParameters',modalParameterChoser]);

})();