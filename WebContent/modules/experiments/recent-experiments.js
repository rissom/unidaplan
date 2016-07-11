(function(){
'use strict';

var recentExperimentsController = function(recentExperiments) {
		
	this.recentExperiments = recentExperiments;

};
    
        
angular.module('unidaplan').controller('recentExperimentsController',['recentExperiments',recentExperimentsController]);

})();