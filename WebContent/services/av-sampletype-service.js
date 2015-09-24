(function(){
'use strict';

var avSampleTypeService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.
	
	
	 // return the translated name string of a type for a sample
	  this.getType=function(sample,types){
		var typeName
		  angular.forEach(types,function(type) {
			if (sample.typeid==type.id){
			    typeName=type.namef();
			}
	      })
		return typeName;
	  }
	
	
	
	this.getSampleTypes = function() {
        var defered=$q.defer();
    	var thisController=this;
    	var promise = restfactory.GET("sampletypes");
    	promise.then(function(rest) {
	    	thisController.sampleTypes = rest.data.sampletypes;
		    thisController.strings = rest.data.strings;
	    	angular.forEach(thisController.sampleTypes,function(sampleType) {
	    		sampleType.namef=function(){
					return (key2string.key2string(sampleType.string_key,thisController.strings))
				}
	    		sampleType.nameLang=function(lang){
					return (key2string.key2stringWithLangStrict(sampleType.string_key,thisController.strings,lang))
				}
	    		sampleType.descf=function(){
					return (key2string.key2string(sampleType.description,thisController.strings))
				}
	    		sampleType.descLang=function(lang){
					return (key2string.key2stringWithLangStrict(sampleType.description,thisController.strings,lang))
				}
				angular.forEach(sampleType.recipes, function(recipe) {
					recipe.namef=function(){
						return (key2string.key2string(recipe.name,thisController.strings));
					}
				})
	      })
    	  defered.resolve(thisController.sampleTypes)		
	    	
    	}, function(rest) {
    		console.log("Error loading sampletypes");
    	});
		return defered.promise;
	}
      

	
	this.translate = function() {
		var thisController=this;
		angular.forEach(thisController.sampleTypes, function(sampletype) {
			sampletype.trname=key2string.key2string(sampletype.string_key,thisController.strings);
		})
	}
	
}


angular.module('unidaplan').service('avSampleTypeService', ['restfactory','$q','$translate','key2string',avSampleTypeService]);

})();