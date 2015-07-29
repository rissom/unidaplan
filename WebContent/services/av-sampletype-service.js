(function(){
'use strict';

var avSampleTypeService = function (restfactory,$q,$translate) {
	// restfactory is a wrapper for $html.

//	this.sampleTypes = ["menno"];
	this.strings = [];


	this.loadSampletypes = function(id) {
		
	};

	
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
	
	
	this.getTypes = function() {
        var defered=$q.defer();
        var now = new Date();
        console.log("now: ",now);
        console.log("last time:",this.lastTimeLoaded)
    	    if  ((this.loaded)&&((now-this.lastTimeLoaded)<5*60*1000)){
    	    	console.log("not loading")
    	  	    defered.resolve(this.sampleTypes)
    	    }else{
    	    	var thisController=this;
    	    	var promise = restfactory.GET("sampletypes.json");
    	    	console.log("loading sampletypes")
    	    	promise.then(function(rest) {
	    	    	thisController.sampleTypes = rest.data.sampletypes;
	    	    	thisController.strings = rest.data.strings;
	    	    	thisController.lastTimeLoaded=new Date();
	    	    	thisController.translate();
	    	    	thisController.loaded=true;
	    	    	defered.resolve(thisController.sampleTypes)
    	    	}, function(rest) {
    	    		console.log("Error loading sampletypes");
    	    	});
    	    }	
		return defered.promise;
	}
      

	
	this.translate = function() {
		var thisController=this;
		angular.forEach(thisController.sampleTypes, function(sampletype) {
			sampletype.trname=thisController.stringFromKey(sampletype.string_key,thisController.strings);
		})
	}
	
}


angular.module('unidaplan').service('avSampleTypeService', ['restfactory','$q','$translate',avSampleTypeService]);

})();