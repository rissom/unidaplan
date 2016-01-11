(function(){
'use strict';

function resultController(restfactory,result,$state,$translate,avSampleTypeService,avProcessTypeService,types,pTypes,
		key2string,languages){
		
	var thisController = this;
	
	this.result = result;
	

	
	
	this.getType = function(type){
		return avSampleTypeService.getType({typeid:type},types);
	};

  
	this.getProcessType = function(type){
		return avProcessTypeService.getProcessType ({typeid:type},pTypes);
	};
	
    
    var reload=function() {
    	var current = $state.current;
    	var params = angular.copy($stateParams);
    	params.newSearch=false;
    	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };
}  


angular.module('unidaplan').controller('resultController',['restfactory','result','$state','$translate',
                             'avSampleTypeService','avProcessTypeService','types','pTypes','key2string','languages',resultController]);

})();