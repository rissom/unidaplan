(function(){
'use strict';

var sampleService = function(restfactory,key2string,avSampleTypeService,$q){
// How to build the ActivityService using the .service method

	var thisController=this;
	this.recentSamples = [];
	this.types = [];
	this.strings =[];


	
	this.loadSample = function(id) {			// Load data and filter Titleparameters
    	var defered=$q.defer()
		var promise = restfactory.GET("showsample?id="+id);
	    promise.then(function(rest) {			// save the sample for recent samples
	        var sample = {"id"		  	   : rest.data.id,
		    			   "typeid"		   : rest.data.typeid,
		    			   "typestringkey" : rest.data.typestringkey,
		    			   "name"		   : rest.data.name,
		    			   "trtype"		   : avSampleTypeService.getType(rest.data.typeid)} // TODO: Weg damit???
		    thisController.pushSample(sample);
	        thisController.sample = rest.data;
	        thisController.translate();
		    defered.resolve(thisController.sample);
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
		return restfactory.DELETE("delete-sample?id="+id);
	}
	
	
	this.getSamplesByName = function (name,details){
		return restfactory.POST('/samples_by_name?name='+name,details);
	}
	
	
	this.saveParameter = function(parameter) {
		return restfactory.POST('savesampleparameter',parameter);
	}
	
	
	
	this.addSampleParameter = function(id,parameter){
		return restfactory.POST('add-sample-parameter?sampleid='+id,parameter);
	}
	
	
	
	this.updateSampleParameter = function(parameter){
		return restfactory.POST('update-sample-parameter',parameter);
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
	
	
	
	this.addSampleType = function(newSampleType){
		return restfactory.POST("add-sample-type",newSampleType); 
	}
	
	this.deleteSampleType=function(id){
		return restfactory.DELETE("delete-sample-type?id="+id);
	};

	
	this.translate = function() {
		angular.forEach(this.sample.parameters, function(parameter) {
			parameter.namef=function(){
				return key2string.key2string(parameter.stringkeyname,thisController.sample.strings) 
			}
			if (parameter.unit){
				parameter.unitf=function(){
					return key2string.key2string(parameter.unit,thisController.sample.strings) 
				}
			}
		});
		angular.forEach(this.sample.plans, function(plan) {
			plan.namef=function(){
				return key2string.key2string(plan.name,thisController.sample.strings)
			} 
			plan.notef=function(){
				return key2string.key2string(plan.note,thisController.sample.strings)
			}
			angular.forEach(plan.plannedprocesses, function(process) {
				if (process.note!=undefined) {
					process.trnote=key2string.key2string(process.note,thisController.sample.strings);
				}
				if (process.recipename!=undefined) {
					process.trrecipe=key2string.key2string(process.recipename,thisController.sample.strings);
				}
			})
		})
	}
}


angular.module('unidaplan').service('sampleService', ['restfactory','key2string','avSampleTypeService','$q',sampleService]);

})();