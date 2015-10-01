(function(){
'use strict';

var avProcessTypeService = function (restfactory,$q,key2string,$translate) {
	// get the available processtypes and their names and recipes.

	
	
	
	
	this.getProcessType = function(process,pTypes) {
		var processTypeName
		  angular.forEach(pTypes,function(ptype) {
			if (process.processtype==ptype.id){
				processTypeName=ptype.namef();
			}
	      })
		return processTypeName; 
	}

	
	
	this.getPTypeParamGrps = function(processTypeID) {
		console.log("processTypeID",processTypeID)
		var defered=$q.defer();
        var thisController=this;
	    var promise = restfactory.GET("process-type-param-grps?processtypeid="+processTypeID);
	    promise.then(function(rest) {
	    	thisController.processType = rest.data;
	    	thisController.strings = rest.data.strings;
	    	thisController.processType.nameLang=function(lang){
    			return (key2string.key2stringWithLangStrict(thisController.processType.name,thisController.strings,lang))
	    	}
	    	thisController.processType.descLang=function(lang){
//	    		return thisController.processType.description
    			return (key2string.key2stringWithLangStrict(thisController.processType.description,thisController.strings,lang))
	    	}
	    	angular.forEach(thisController.processType.parametergrps,function(ptgrp) {
	    		ptgrp.namef=function(){
	    			return (key2string.key2string(ptgrp.stringkey,thisController.strings))
	    		}
	    		ptgrp.nameLang=function(lang){
	    			return (key2string.key2stringWithLangStrict(ptgrp.stringkey,thisController.strings,lang))
	    		}
	    		
	         })
	    	defered.resolve(thisController.processType)	    	
		    }, function(rest) {
			console.log("Error loading processtypes");
		 });
	    return defered.promise;
	}
	
	
	
	this.getProcessRecipes = function(process,pTypes){
		var recipes=[];
		angular.forEach(pTypes,function(ptype) {
			if (process.processtype==ptype.id){
				recipes=ptype.recipes;
			}
	      })
		return recipes
	}
	
	
	
	this.getProcessTypes = function() {
        var defered=$q.defer();
        var thisController=this;
	    var promise = restfactory.GET("available-processtypes");
	    promise.then(function(rest) {
	    	thisController.processTypes = rest.data.processes;
	    	angular.forEach(thisController.processTypes,function(ptype) {
	    		ptype.namef=function(){
	    			return (key2string.key2string(ptype.name,thisController.strings))
	    		}
	    		ptype.nameLang=function(lang){
	    			return (key2string.key2stringWithLangStrict(ptype.name,thisController.strings,lang))
	    		}
	    		ptype.descf=function(){
	    			return (key2string.key2string(ptype.description,thisController.strings))
	    		}
	    		ptype.descLang=function(lang){
	    			return (key2string.key2stringWithLangStrict(ptype.description,thisController.strings,lang))
	    		}
	    		angular.forEach(ptype.recipes, function(recipe) {
	    			recipe.namef=function(){
	    				return (key2string.key2string(recipe.name,thisController.strings));
	    			}
	    		})
	         })
	         thisController.strings = rest.data.strings;
	    	thisController.loaded=true;
	    	defered.resolve(thisController.processTypes)	    	
		    }, function(rest) {
			console.log("Error loading processtypes");
		 });
	    return defered.promise;
	}




}


angular.module('unidaplan').service('avProcessTypeService', ['restfactory','$q','key2string','$translate',avProcessTypeService]);

})();