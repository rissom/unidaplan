(function(){
'use strict';

var sampleService = function(restfactory,avSampleTypeService,$q){
// How to build the ActivityService using the .service method

	var thisController=this;
	this.recentSamples = [];
	this.types = [];
	this.strings =[];

	
	this.loadSample = function(id) {			// Load data and filter Titleparameters
    	var defered=$q.defer()
		var promise = restfactory.GET("showsample.json?id="+id);
	    promise.then(function(rest) {			// save the sample for recent samples
	        var sample = {"id"		  	   : rest.data.id,
		    			   "typeid"		   : rest.data.typeid,
		    			   "typestringkey" : rest.data.typestringkey,
		    			   "name"		   : rest.data.name,
		    			   "trtype"		   : avSampleTypeService.getType(rest.data.typeid)} // TODO: Weg damit???
		    thisController.pushSample(sample);
		    defered.resolve(rest.data);
	    }, function(rest) {
	    	console.log("Sample not found");
	    	defered.reject({"error":"Not Found!"});
	    });
	    return defered.promise;
	}

	
	
	this.addAncestors= function(sampleID,ancestors){
		return restfactory.POST('add-ancestors',{"sampleid":sampleID,"ancestors":ancestors});
	}
	
	
	
	this.addChildren= function(sampleID,children){
		return restfactory.POST('add-children',{"sampleid":sampleID,"children":children});
	}
	
	
	// delete a sample (also from recent samples)
	this.deleteSample = function(id){
		for (var i=0;i<this.recentSamples.length;i++){
			if (this.recentSamples[i].id==id){
				this.recentSamples.splice(i,1);
			}
		}
		return restfactory.POST("delete-sample?id="+id);
	}
	
	
	
	this.saveParameter = function(parameter) {
		return restfactory.POST('savesampleparameter.json',parameter);
	}
	
	
	
	this.addSampleParameter = function(id,parameter){
		return restfactory.POST('add-sample-parameter.json?sampleid='+id,parameter);
	}
	
	
	
	this.updateSampleParameter = function(parameter){
		return restfactory.POST('update-sample-parameter.json',parameter);
	}

	
	
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


angular.module('unidaplan').service('sampleService', ['restfactory','avSampleTypeService','$q',sampleService]);

})();