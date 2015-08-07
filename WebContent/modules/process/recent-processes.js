(function(){
'use strict';

var recentProcessController = function(ptypes,avProcessTypeService,processService) {

	this.getRecentSamples=function(){
		return processService.recentProcesses;
	}
	
	this.getType = function(sample){
		return avProcessTypeService.getType(process,ptypes);
	}
	
};
    
        
angular.module('unidaplan').controller('recentProcessController',['ptypes','avProcessTypeService','processService',recentProcessController]);

})();