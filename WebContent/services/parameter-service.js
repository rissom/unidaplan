(function(){
'use strict';

var parameterService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.	
	
	
	this.addPossibleValue = function (value, parameterID){
		return restfactory.POST("add-possible-value",{value:value, parameterid:parameterID})
	};

	
	
	this.addParameter = function (parameter){
		return restfactory.POST("add-parameter",parameter);
	};
	

	
	this.deleteParameter = function (id){
		return restfactory.DELETE("delete-parameter?id="+id);
	};
	
	
	
	this.deletePossibleValue = function(id){
		return restfactory.DELETE("delete-possible-value?id="+id);
//		console.log("possibleValueId: "+possibleValueId)
	}
	
	
	
	this.getParameters = function() {
        var defered=$q.defer();
    	var thisController = this;
    	var promise = restfactory.GET("parameter");
    	promise.then(function(rest) {
	    	thisController.parameters = rest.data.parameters;
	    	thisController.strings = rest.data.strings;
	    	angular.forEach(thisController.parameters, function(parameter) {
				parameter.nameLang=function(lang){
					return key2string.key2stringWithLangStrict(parameter.stringkeyname,thisController.strings,lang);
				};
				parameter.namef = function(){
					return key2string.key2string(parameter.stringkeyname,thisController.strings);
				};
				parameter.actions = [{action : "edit", namef : function(){ return $translate.instant("edit")}},
				  	    		     {action : "delete", namef : function(){ return $translate.instant("delete")}, disabled : !parameter.deletable}
				  				    ];
				parameter.nameUnitf = function(){
					var unit = "";
					if (parameter.stringkeyunit){
						unit=key2string.key2string(parameter.stringkeyunit,thisController.strings);
						if (unit.length > 0){
							unit=" ("+unit+")";
						}
					}
					return key2string.key2string(parameter.stringkeyname,thisController.strings) + unit;
				};
				parameter.unitLang = function(lang){
					if (['integer','measurement','float'].indexOf(parameter.datatype) === -1) { return '-';}
					return key2string.key2stringWithLangStrict(parameter.stringkeyunit,thisController.strings,lang);
				};	
				parameter.unitf = function(){
					return key2string.key2string(parameter.stringkeyunit,thisController.strings);
				};	
				parameter.descLang = function(lang){
					var ergebnis= key2string.key2stringWithLangStrict(parameter.description,thisController.strings,lang);
					return ergebnis;
				};
				parameter.descf = function(){
					return key2string.key2string(parameter.description,thisController.strings);
				};	
	    	});
	    	defered.resolve(thisController.parameters);
    	}, function(rest) {
    		console.log("Error loading parameters");
    	});
		return defered.promise;
	};

	
	
	this.reorderPossibleValues = function(parameterid, neworder){
		return restfactory.PUT("reorder-possible-values",{parameterid:parameterid,neworder:neworder})
	}
	
	
	
	this.updateParameter = function(param){
		return restfactory.PUT("update-parameter", param);
	}
	
	
	
	this.updatePossibleValue = function(pvalueid,newValue){
		return restfactory.PUT("update-possible-value",{id:pvalueid,newvalue:newValue});
	}
	
};


angular.module('unidaplan').service('parameterService', ['restfactory','$q','$translate','key2string',parameterService]);

})();