(function(){
'use strict';

var recentExperimentsController = function(experimentService) {
			
	this.getRecentExperiments=function(){
		return experimentService.recentExperiments;
	}

};
    
        
angular.module('unidaplan').controller('recentExperimentsController',['experimentService',recentExperimentsController]);

})();