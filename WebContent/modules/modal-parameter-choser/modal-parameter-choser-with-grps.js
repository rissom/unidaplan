(function(){
'use strict';


function modalParameterChoserGrps($translate,$uibModalInstance,languages,restfactory,avParameters,paramGroups,parameters) {

	
	this.avParameters=avParameters;
	
	this.paramGroups=paramGroups;

	var thisController=this;
	
	this.cancel=function(){ // Parameters where not changed
	    $uibModalInstance.close({chosen:[]});
	};
	
	if (parameters) {
		this.selectedParameters=parameters.slice(0);
	} else {
		this.selectedParameters=[];
	}

	
	this.assignParameters=function(){    // pass the new list of parameters if it has changed
	    $uibModalInstance.close({chosen: this.selectedParameters, inParams:parameters});
	};
	
	
	
	this.showParamGrp=function(parameter){
		for (var i=0;i<thisController.paramGroups.length;i++)
			if (parameter.parametergroup===thisController.paramGroups[i].id)
				return this.paramGroups[i].namef();
	};
	
};

        
angular.module('unidaplan').controller('modalParameterChoserGrps',['$translate','$uibModalInstance','languages',
                            'restfactory','avParameters','paramGroups','parameters',modalParameterChoserGrps]);

})();