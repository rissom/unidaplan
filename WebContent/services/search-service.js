(function(){
'use strict';

var searchService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.

	var thisController=this;
	
	
	this.getExperiments = function() {
        var defered=$q.defer();
		var promise = restfactory.GET("experiments");
		 		
	    promise.then(function(rest) {
	    	thisController.experiments = rest.data.experiments;
	    	thisController.expsStrings = rest.data.strings;
	    	
	    	
			angular.forEach(thisController.experiments, function(anExp) {
				anExp.namef=function(){return key2string.key2string(anExp.name,thisController.expsStrings)}
			})
	    	
	    	
	    	defered.resolve(thisController.experiments)
	    }, function(rest) {
	    });
		return defered.promise;
	};
	
	
	
	this.getExperiment = function(id) {
        var defered=$q.defer();
			var promise = restfactory.GET("experiment?id="+id);
	    	promise.then(function(rest) {
    	    	thisController.experiment = rest.data.experiment;
    	    	thisController.strings = rest.data.strings;
    			thisController.experiment.namef=function(){
    				return key2string.key2string(thisController.experiment.name,thisController.strings);
    			}
    			angular.forEach(thisController.experiment.parameters, function(parameter) {
    				parameter.namef=function(){
    					return key2string.key2string(parameter.stringkeyname,thisController.strings)
    				}
    				parameter.nameLang=function(lang){				
    					return key2string.key2stringWithLangStrict(parameter.stringkeyname,thisController.strings,lang)
    				}
     				parameter.unitf=function(){
    					return key2string.unitf(parameter.stringkeyunit,thisController.strings)
    				}
    				parameter.unitLang=function(lang){				
    					return key2string.key2stringWithLangStrict(parameter.stringkeyunit,thisController.strings,lang)
    				}
    				if (parameter.datatype==="date+time") {
    					parameter.date=new Date(parameter.value)
    				}
//    				parameter.trname=key2string.key2string(parameter.stringkeyname,thisController.strings);
    			})
    			angular.forEach(thisController.experiment.samples, function(sample){
    				if (sample.note!=undefined) {
    					sample.trnote=key2string.key2string(sample.note,thisController.strings);
    				}
    				angular.forEach(sample.pprocesses, function(pprocess){
    					if (pprocess.note){
    						pprocess.trnote=key2string.key2string(pprocess.note,thisController.strings);
    					}
    				})
    			});
    			thisController.pushExperiment(thisController.experiment);
    	    	defered.resolve(thisController.experiment)
	    	}, function(rest) {    	    		
	    		console.log("Error loading experiment");
	    		defered.reject({"error":"Error loading experiment"});
	    	});
		return defered.promise;
	}
	
	
	
	this.ExpStepChangeRecipe = function(id,recipe) {
		return restfactory.POST("exp-step-change-recipe?processstepid="+id+"&recipe="+recipe);
	}
	
    
	
	this.getSearches = function(experiment){
		return 0
	}
	
	
	this.addSearch = function(experiment){

	}
	
	
	
	// delete an experiment (also from recent experiments)
	this.deleteSearch = function(searchID){
		return restfactory.DELETE("delete-search?searchid="+searchID);
	}
	
	
	
	
}


angular.module('unidaplan').service('searchService', ['restfactory','$q','$translate','key2string',searchService]);

})();