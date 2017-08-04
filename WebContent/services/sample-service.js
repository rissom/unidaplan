(function(){
'use strict';

var sampleService = function(restfactory,key2string,avSampleTypeService,$q){
// How to build the ActivityService using the .service method

	var thisController = this;
	this.recentSamples = [];
	this.types = [];
	this.strings =[];

	
	
	this.addAncestors= function(sampleID,ancestors){
		return restfactory.POST('add-ancestors',{"sampleid":sampleID,"ancestors":ancestors});
	};
	
	
	
	this.addChildren= function(sampleID,children){
		return restfactory.POST('add-children',{"sampleid":sampleID,"children":children});
	};
	

	
	this.addSample = function(sampletype, recipe){
		var d = new Date();
		var json = { sampletypeid : sampletype,
					 tz : d.getTimezoneOffset(),
					 date : d }
		if (recipe > 0) { 
			json.recipe = recipe 
		} 
		return restfactory.POST("add-sample",json);
	}
	

	
	this.addSampleRecipe = function(name,sampletype){
		var newRecipe = {"name" : name, "sampletype" : sampletype, "type" : "sample"};
		return restfactory.POST("add-recipe",newRecipe);
	}
	
	
	
	this.addSampleType = function(newSampleType){
		return restfactory.POST("add-sample-type",newSampleType); 
	};
	
	

	// delete an attached file
	this.deleteFile = function(fileID){
		return restfactory.DELETE("delete-file?fileid="+fileID)
	}
	
	
	
	// delete a sample (also from recent samples)
	this.deleteSample = function(id){
		for (var i=0;i<this.recentSamples.length;i++){
			if (this.recentSamples[i].id==id){
				this.recentSamples.splice(i,1);
			}
		}
		return restfactory.DELETE("delete-sample?id="+id);
	};
	
	

	this.deleteSampleRecipe = function(recipeID){
		return restfactory.DELETE("delete-sample-recipe?id=" + recipeID);
	}
	
	
	
	this.deleteSampleType=function(id){
		return restfactory.DELETE("delete-sample-type?id="+id);
	};

	
	

	this.getRecipe = function(recipeID){
		var defered = $q.defer();
		var promise = restfactory.GET('/get-sample-recipe?recipeid='+recipeID);
		promise.then(function(rest) {			// getting the parameters
			var strings = rest.data.strings;
			angular.forEach(rest.data.parametergroups, function(paramgrp) {
				paramgrp.grpnamef = function(){
					return key2string.key2string(paramgrp.paramgrpkey,strings);
				};
				angular.forEach(paramgrp.parameter, function(parameter) {
					parameter.namef = function(){
						return key2string.key2string(parameter.stringkeyname,strings);
					};
					if (parameter.parametergroup){
						parameter.grpnamef = function(){
							return key2string.key2string(parameter.parametergrp_key,strings);
						};
					}
					if (parameter.unit){
						parameter.unitf = function(){
							return key2string.key2string(parameter.unit,strings); 
						};
					}
					if (parameter.datatype === "date" || parameter.datatype === "timestamp"){			
						parameter.newDate = new Date(parameter.value);
					}
				});
			});
			rest.data.namef = function(){
				return key2string.key2string(rest.data.name,strings);
			}
			rest.data.nameLang = function(lang){
				return key2string.key2stringWithLangStrict(rest.data.name,strings,lang);
			}
			defered.resolve(rest.data);
		}, function () {
			console.log("Error getting Parameters")
		}); // promise.then
	    return defered.promise;	
	}
	
	
	
	
	this.getSamplesByName = function (name,types,privilege){
		// possible values for privilege: "r"-> read or write; "w"-> write; "a" -> all
		var params = {
			sampletypes : types,
			name : name,
			privilege : privilege
		};
		return restfactory.POST('/samples_by_name',params);
	};
	
	
	
	this.getSampleTypeParameters = function (sampleTypeID){
    	var defered=$q.defer();
		var promise = restfactory.GET('/all-sample-type-params?sampletypeid='+sampleTypeID);
		promise.then(function(rest) {			// getting the parameters
			var parameters = {parameters:rest.data.parameters,parametergrps:rest.data.parametergrps};
			var strings = rest.data.strings;
			angular.forEach(parameters.parameters,function(parameter){
				parameter.namef = function(){
					return key2string.key2string(parameter.name,strings);
				}				
			});
			angular.forEach(parameters.parametergrps,function(grp){
				grp.namef = function(){
					return key2string.key2string(grp.stringkey,strings);
				}
			});
			defered.resolve(parameters);
		}, function () {
			console.log("Error getting Parameters")
		}); // promise.then
	    return defered.promise;
	}
	
	
	
	this.loadSample = function(id) {			// Load data and filter Titleparameters
    	var defered=$q.defer();
		var promise = restfactory.GET("showsample?id="+id);
	    promise.then(function(rest) {			// save the sample for recent samples
	        var sample = { "id"		  	   : rest.data.id,
		    			   "typeid"		   : rest.data.typeid,
		    			   "typestringkey" : rest.data.typestringkey,
		    			   "name"		   : rest.data.name};
		    thisController.pushSample(sample);
	        thisController.sample = rest.data;
	        var strings = rest.data.strings;
	        
	    	// add translation functions
			angular.forEach(thisController.sample.parametergroups, function(paramgrp) {
				paramgrp.grpnamef=function(){
					return key2string.key2string(paramgrp.paramgrpkey,strings);
				};
				angular.forEach(paramgrp.parameter, function(parameter) {
					parameter.namef = function(){
						return key2string.key2string(parameter.namekey,strings);
					};
					if (parameter.parametergroup){
						parameter.grpnamef=function(){
							return key2string.key2string(parameter.parametergrp_key,strings);
						};
					}
					if (parameter.unit){
						parameter.unitf = function(){
							return key2string.key2string(parameter.unit,strings);
						};
					}
					if (parameter.datatype==="date" || parameter.datatype==="timestamp"){			
						parameter.newDate=new Date(parameter.value);
					}
				});
			});
			angular.forEach(thisController.sample.plans, function(plan) {
				plan.namef=function(){
					return key2string.key2string(plan.name,thisController.sample.strings);
				};
				plan.notef=function(){
					return key2string.key2string(plan.note,thisController.sample.strings);
				};
				angular.forEach(plan.plannedprocesses, function(process) {
					if (process.note!==undefined) {
						process.trnote=key2string.key2string(process.note,thisController.sample.strings);
					}
					if (process.recipename!==undefined) {
						process.trrecipe=key2string.key2string(process.recipename,thisController.sample.strings);
					}
				});
			});
			defered.resolve(thisController.sample);
	    }, function(rest) {
	    	console.log("Sample not found");
	    	defered.reject({"error":"Not Found!"});
	    });
	    return defered.promise;
	};
	
	
	
	this.pushSample = function(sample){
		var i;
		var found=false;
		for (i=0;i<this.recentSamples.length;i++){
			if (this.recentSamples[i].name==sample.name){
				found=true;	
			}
		}
		if (!found) {
			this.recentSamples.push(sample);
		}
		if (this.recentSamples.length>20){
			this.recentSamples.slice(0,this.recentSamples.length-20);
		}
	};
	
	

	this.saveParameter = function(sampleid,parameter) {
		var json = {sampleid:sampleid, parameterid:parameter.pid, data:parameter.data};
		if ("date" in parameter) {
			json.date=parameter.date;
			json.tz=parameter.tz;
		}
		if ("error" in parameter) {
			json.error=parameter.error;
		} 
		return restfactory.POST('save-sample-parameter',json);
	};
	
	
	
	this.saveSampleRecipeParameter = function(samplerecipeid,parameter) {
		var json = {samplerecipeid:samplerecipeid, parameterid:parameter.id, data:{value:parameter.data.value}};
		if ("date" in parameter.data) {
			json.data.date = parameter.data.date;
			json.data.tz = parameter.data.tz;
		}
		if ("error" in parameter.data) {
			json.data.error = parameter.data.error;
		} 
		return restfactory.POST('save-sample-recipe-parameter',json);
	};
	
	
	
	this.updateSampleRecipeName = function(id, newName, language){
		var json = {'type':'sample', 
					'language':language,
					'id':id,
					'name':newName};
		return restfactory.PUT('update-recipe-name',json);
	}

	
};


angular.module('unidaplan').service('sampleService', ['restfactory','key2string','avSampleTypeService','$q',sampleService]);

})();