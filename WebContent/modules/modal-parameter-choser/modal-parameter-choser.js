(function(){
'use strict';


function modalParameterChoser($translate,$uibModalInstance,languages,restfactory,avParameters,parameters) {
	
	this.avParameters=avParameters;

	var thisController=this;	
	

	
	this.cancel=function(){ // Parameters where not changed
	    $uibModalInstance.close({chosen:[]});
	};
	
	
	
	this.assignParameters=function(){    // pass the new list of parameters if it has changed
	    $uibModalInstance.close({chosen: this.selectedParameters});
	};
}

        
angular.module('unidaplan').controller('modalParameterChoser',['$translate','$uibModalInstance','languages',
                            'restfactory','avParameters','parameters',modalParameterChoser]);

})();