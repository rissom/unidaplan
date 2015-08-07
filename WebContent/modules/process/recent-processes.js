(function(){
'use strict';

var recentProcessController = function(ptypes,avProcessTypeService,processService) {

	this.getRecentProcesses=function(){
		return processService.recentProcesses;
	}
	
	this.getType = function(process){
		return avProcessTypeService.getProcessType(process,ptypes);
	}
	
};
    
        
angular.module('unidaplan').controller('recentProcessController',['ptypes','avProcessTypeService','processService',recentProcessController]);

})();