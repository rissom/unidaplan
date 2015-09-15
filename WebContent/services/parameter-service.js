(function(){
'use strict';

var avParameterService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.


	
	
	 // return the translated name string of a type for a sample
	  this.getType=function(sample,types){
		var typeName
		  angular.forEach(types,function(type) {
			if (sample.typeid==type.id){
			    typeName=type.trname;
			}
	      })
		return typeName;
	  }
	
	
	
	this.getParameters = function() {
        var defered=$q.defer();
        var now = new Date();
    	    if  ((this.loaded)&&((now-this.lastTimeLoaded)<5*60*1000)){
    	    	this.translate();
    	  	    defered.resolve(this.sampleTypes)
    	    }else{
    	    	var thisController=this;
    	    	var promise = restfactory.GET("parameter");
    	    	promise.then(function(rest) {
	    	    	thisController.parameters = rest.data.parameters;
	    	    	thisController.strings = rest.data.strings;
	    	    	thisController.lastTimeLoaded=new Date();
	    	    	thisController.translate();
	    	    	thisController.loaded=true;
//	    	    	thisController.sampleTypes.getType=thisController.getType;
	    	    	defered.resolve(thisController.parameters)
    	    	}, function(rest) {
    	    		console.log("Error loading parameters");
    	    	});
    	    }	
		return defered.promise;
	}
      

	
	this.translate = function() {
		var thisController=this;
		angular.forEach(thisController.sampleTypes, function(sampletype) {
			sampletype.trname=key2string.key2string(sampletype.string_key,thisController.strings);
		})
	}
	
}


angular.module('unidaplan').service('avParameterService', ['restfactory','$q','$translate','key2string',avParameterService]);

})();