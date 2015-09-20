(function(){
'use strict';

var parameterService = function (restfactory,$q,$translate,key2string,languages) {
	// restfactory is a wrapper for $html.


	
	
//	 // return the translated name string of a type for a sample
//	  this.getType=function(sample,types){
//		var typeName
//		  angular.forEach(types,function(type) {
//			if (sample.typeid==type.id){
//			    typeName=type.trname;
//			}
//	      })
//		return typeName;
//	  }
	
	
	
	this.getParameters = function() {
        var defered=$q.defer();
    	var thisController=this;
    	var promise = restfactory.GET("parameter");
    	promise.then(function(rest) {
	    	thisController.parameters = rest.data.parameters;
	    	thisController.strings = rest.data.strings;
	    	thisController.translate();
	    	defered.resolve(thisController.parameters)
    	}, function(rest) {
    		console.log("Error loading parameters");
    	});
		return defered.promise;
	} 

	
	this.addParameter = function (parameter){
		var promise=restfactory.POST("add-parameter",parameter);
		console.log("parameter:",parameter)
		return promise;
	}

	
	this.deleteParameter = function (id){
		return restfactory.DELETE("delete-parameter?id="+id);
	}
	
	this.translate = function() {
		var thisController=this;
		angular.forEach(thisController.parameters, function(parameter) {
			parameter.nameLang1=key2string.replace(key2string.key2stringWithLangStrict(
				parameter.stringkeyname,thisController.strings,languages[0].key));
			parameter.nameLang2=key2string.replace(key2string.key2stringWithLangStrict(
				parameter.stringkeyname,thisController.strings,languages[1].key));
			parameter.unitLang1=key2string.replace(key2string.key2stringWithLangStrict(
				parameter.stringkeyunit,thisController.strings,languages[0].key));
			parameter.unitLang2=key2string.replace(key2string.key2stringWithLangStrict(
				parameter.stringkeyunit,thisController.strings,languages[1].key));
			parameter.descLang1=key2string.replace(key2string.key2stringWithLangStrict(
				parameter.id_description,thisController.strings,languages[0].key));
			parameter.descLang2=key2string.replace(key2string.key2stringWithLangStrict(
				parameter.id_description,thisController.strings,languages[1].key));
		})
	}
	
}


angular.module('unidaplan').service('parameterService', ['restfactory','$q','$translate',
                                                           'key2string','languages',parameterService]);

})();