(function(){
'use strict';

var sampleService = function(){
// How to build the ActivityService using the .service method

	this.recentSamples = []

	
	this.pushSample = function(sample){
		var i;
		var found=false;
		for (i=0;i<this.recentSamples.length;i++){
			if (this.recentSamples[i].name==sample.name){
				found=true			
			}
		}
		if (!found) {
			this.recentSamples.push(sample);
		}
		if (this.recentSamples.length>20){
			this.recentSamples.slice(0,this.recentSamples.length-20);
		}
	}
	
	
}


angular.module('unidaplan').service('sampleService', sampleService);

})();