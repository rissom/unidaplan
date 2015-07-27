(function(){
'use strict';

var recentSampleController = function(sampleService,$state) {

	this.getRecentSamples=function(){
		return sampleService.recentSamples;
	}
	
};
    
        
angular.module('unidaplan').controller('recentSampleController',['sampleService','$state',recentSampleController]);

})();