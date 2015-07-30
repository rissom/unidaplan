(function(){
'use strict';

var avSampleTypeService = function (restfactory,$q,$translate) {
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
	
	
	
	 // return the translated name string of a type for a sample
	  this.getType=function(sample,types){
//		  console.log
		var typeName
		  angular.forEach(types,function(type) {
			if (sample.typeid==type.id){
			    typeName=type.trname;
			}
	      })
		return typeName;
	  }
	
	
	
	this.getTypes = function() {
        var defered=$q.defer();
        var now = new Date();
    	    if  ((this.loaded)&&((now-this.lastTimeLoaded)<5*60*1000)){
    	    	this.translate();
    	  	    defered.resolve(this.sampleTypes)
    	    }else{
    	    	var thisController=this;
    	    	var promise = restfactory.GET("sampletypes.json");
    	    	promise.then(function(rest) {
	    	    	thisController.sampleTypes = rest.data.sampletypes;
	    	    	thisController.strings = rest.data.strings;
	    	    	thisController.lastTimeLoaded=new Date();
	    	    	thisController.translate();
	    	    	thisController.loaded=true;
	    	    	thisController.sampleTypes.getType=thisController.getType;
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