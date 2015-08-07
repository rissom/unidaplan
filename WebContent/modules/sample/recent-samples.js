(function(){
'use strict';

var recentSampleController = function(types,avSampleTypeService,sampleService) {

	this.getRecentSamples=function(){
		return sampleService.recentSamples;
	}
	
	this.getType = function(sample){
		return avSampleTypeService.getType(sample,types);
	}
	
	
};
    
        
angular.module('unidaplan').controller('recentSampleController',['types','avSampleTypeService','sampleService',recentSampleController]);

})();