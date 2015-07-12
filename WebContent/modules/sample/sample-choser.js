(function(){
'use strict';

function sampleChoser(restfactory,sampleService,$translate,$scope) {
	
	this.mysamples =  [{id:1},{id:2},{id:3}];
	
	this.samples = function(name)
	{
//		console.log(SampleService.loadSamplesByName(2,""))	
		console.log("working");
		return this.mysamples
//		return SampleService.loadSamplesByName(1,name);
//		return sampleService.samples;
		// to test: http://localhost:8080/unidaplan/samples_by_name.json?type=1&name=1
	};

};
    
        
angular.module('unidaplan').controller('sampleChoser',['sampleService','$translate','$scope',sampleChoser]);

})();