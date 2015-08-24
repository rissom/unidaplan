(function(){
'use strict';

var recentExperimentsController = function(experimentService,$translate) {
		
	this.key=2;
	
	this.getRecentExperiments=function(){
		console.log("hallo");
		return experimentService.recentExperiments;
	}

};
    
        
angular.module('unidaplan').controller('recentExperimentsController',['experimentService','$translate',recentExperimentsController]);

})();