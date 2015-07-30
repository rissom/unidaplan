(function(){
'use strict';

var processService = function (restfactory,$q,$translate) {
	// restfactory is a wrapper for $html.


	this.stringFromKey = function(stringkey,strings) {
		var keyfound=false;
		var returnString="@@@ no string! @@@";
		angular.forEach(strings, function(translation) {
			if (!keyfound && stringkey==translation.string_key) {
				returnString = translation.value;
				if (translation.language==$translate.use()) {
					keyfound=true;
				}
			}
		})
		return returnString;
	};
	
	
	
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
		
		this.process.trprocesstype = this.stringFromKey(this.process.pt_string_key,this.strings)		
		var thisController=this;
		angular.forEach(thisController.process.parameters, function(parameter) {
			parameter.trname=thisController.stringFromKey(parameter.stringkeyname,thisController.strings);
		})
	}
	
}


angular.module('unidaplan').service('processService', ['restfactory','$q','$translate',processService]);

})();