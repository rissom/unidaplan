(function(){
'use strict';


function modalParameterChoserGrps($translate,$modalInstance,languages,restfactory,avParameters,paramGroups,parameters) {

	
	this.avParameters=avParameters;
	
	this.paramGroups=paramGroups;

	var thisController=this;
	
	this.cancel=function(){ // Parameters where not changed
	    $modalInstance.close({chosen:[]});
	};
	
	if (parameters) {
		this.selectedParameters=parameters.slice(0);
	} else {
		this.selectedParameters=[];
	}

	
	this.assignParameters=function(){    // pass the new list of parameters if it has changed
	    $modalInstance.close({chosen: this.selectedParameters});
	};
	
	
	
	this.showParamGrp=function(parameter){
		for (var i=0;i<thisController.paramGroups.length;i++)
			if (parameter.parametergroup===thisController.paramGroups[i].id)
				return this.paramGroups[i].namef();
	};
	
};

        
angular.module('unidaplan').controller('modalParameterChoserGrps',['$translate','$modalInstance','languages',
                            'restfactory','avParameters','paramGroups','parameters',modalParameterChoserGrps]);

})();