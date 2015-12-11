(function(){
'use strict';

var parameterService = function (restfactory,$q,key2string) {
	// restfactory is a wrapper for $html.	
	
	
	this.getParameters = function() {
        var defered=$q.defer();
    	var thisController=this;
    	var promise = restfactory.GET("parameter");
    	promise.then(function(rest) {
	    	thisController.parameters = rest.data.parameters;
	    	thisController.strings = rest.data.strings;
	    	angular.forEach(thisController.parameters, function(parameter) {
				parameter.nameLang=function(lang){
					return key2string.key2stringWithLangStrict(parameter.stringkeyname,thisController.strings,lang);
				};
				parameter.namef=function(){
					return key2string.key2string(parameter.stringkeyname,thisController.strings);
				};
				parameter.nameUnitf=function(){
					var unit="";
					if (parameter.stringkeyunit){
						unit=key2string.key2string(parameter.stringkeyunit,thisController.strings);
						if (unit.length>0){
							unit=" ("+unit+")";
						}
					}
					return key2string.key2string(parameter.stringkeyname,thisController.strings)+unit;
				};
				parameter.unitLang=function(lang){
					return key2string.key2stringWithLangStrict(parameter.stringkeyunit,thisController.strings,lang);
				};	
				parameter.unitf=function(){
					return key2string.key2string(parameter.stringkeyunit,thisController.strings);
				};	
				parameter.descLang=function(lang){
					return key2string.key2stringWithLangStrict(parameter.id_description,thisController.strings,lang);
				};
				parameter.descf=function(){
					return key2string.key2string(parameter.id_description,thisController.strings);
				};	
	    	});
	    	defered.resolve(thisController.parameters);
    	}, function(rest) {
    		console.log("Error loading parameters");
    	});
		return defered.promise;
	};

	
	
	this.addParameter = function (parameter){
		var promise=restfactory.POST("add-parameter",parameter);
		return promise;
	};
	

	
	this.deleteParameter = function (id){
		return restfactory.DELETE("delete-parameter?id="+id);
	};
};


angular.module('unidaplan').service('parameterService', ['restfactory','$q','key2string',parameterService]);

})();