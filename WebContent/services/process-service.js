(function(){
'use strict';

var processService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.

	
	
	this.getProcess = function(id) {
        var defered=$q.defer();
    	    	var thisController=this;
    			var promise = restfactory.GET("process.json?id="+id);
    	    	promise.then(function(rest) {
	    	    	thisController.process = rest.data;
	    	    	thisController.strings = rest.data.strings;
	    	    	thisController.translate();
	    	    	defered.resolve(thisController.process)
    	    	}, function(rest) {
    	    		console.log("Error loading sampletypes");
    	    	});
		return defered.promise;
	}
      

	
	this.translate = function() {
		
		this.process.trprocesstype = key2string.key2string(this.process.pt_string_key,this.strings)		
		var thisController=this;
		angular.forEach(thisController.process.parameters, function(parameter) {
			parameter.trname= key2string.key2string(parameter.stringkeyname,thisController.strings);
		})
	}
	
}


angular.module('unidaplan').service('processService', ['restfactory','$q','$translate','key2string',processService]);

})();