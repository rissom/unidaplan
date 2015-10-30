(function(){
'use strict';


function modalParameterChoser($translate,$modalInstance,languages,restfactory,avParameters) {

	
	this.avParameters=avParameters;

	var thisController=this;
	console.log("av Parameters:",avParameters)	
	
	
	this.cancel=function(){ // Parameters where not changed
	    $modalInstance.close({chosen:[]});
	}
	
	
	
	this.assignParameters=function(){    // pass the new list of parameters if it has changed
	    $modalInstance.close({chosen: this.selectedParameters});
	}
};

        
angular.module('unidaplan').controller('modalParameterChoser',['$translate','$modalInstance','languages',
                            'restfactory','avParameters',modalParameterChoser]);

})();