(function(){
'use strict';

var avProcessTypeService = function (restfactory,$q,key2string,$translate) {
	// get the available processtypes and their names and recipes.

	
	
	this.getProcessType = function(process,pTypes) {
		var processTypeName
		  angular.forEach(pTypes,function(ptype) {
			if (process.processtype==ptype.id){
				processTypeName=ptype.trname;
			}
	      })
		return processTypeName; 
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
        var now = new Date();
	    if  ((this.loaded)&&((now-this.lastTimeLoaded)<5*60*1000)){
	    	this.translate();
	  	    defered.resolve(this.processTypes)
	    }else{
	    	var promise = restfactory.GET("available-processtypes");
	    	promise.then(function(rest) {
	    		thisController.processTypes = rest.data.processes;
	    		thisController.strings = rest.data.strings;
	    		thisController.lastTimeLoaded=new Date();
	    		thisController.translate();
	    		thisController.loaded=true;
	    		defered.resolve(thisController.processTypes)	    	
		   }, function(rest) {
			console.log("Error loading processtypes");
		   });
	    }
	    return defered.promise;
	}

	
	
	this.translate = function() {
        var thisController=this;
		var strings=thisController.strings
		angular.forEach(thisController.processTypes, function(proc) {
			proc.trname=key2string.key2string(proc.name,thisController.strings);
			angular.forEach(proc.recipes, function(recipe) {
				recipe.trname=key2string.key2string(recipe.name,thisController.strings);
			})
		})
	}



}


angular.module('unidaplan').service('avProcessTypeService', ['restfactory','$q','key2string','$translate',avProcessTypeService]);

})();