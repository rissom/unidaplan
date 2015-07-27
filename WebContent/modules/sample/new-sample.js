(function(){
'use strict';

function newSampleController(restfactory,$translate,$scope,$state){
	
	var thisController = this;
	
	this.sampletypes = [];
	
	this.strings = [];
			
	this.loadTypes = function() {			// Load data and filter Titleparameters
		var promise = restfactory.GET("/sampletypes.json");
	    promise.then(function(rest) {
	    	thisController.sampletypes = rest.data.sampletypes;
	    	thisController.strings = rest.data.strings;
	    	thisController.translate($translate.use()); // translate to active language
	        thisController.sampletype=thisController.sampletypes[0];

	    }, function(rest) {
	    	thisController.sample.error = "Not Found!";
	    });
	};
	

	$scope.$on('language changed', function(event, args) {
		thisController.translate(args.language);
	});
	
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
		}
	}

		
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
	
	

	this.translate = function(lang) {
		angular.forEach(this.sampletypes, function(sampletype) {
			sampletype.trname=thisController.stringFromKey(sampletype.string_key,thisController.strings) 
		})
	}

	
	this.addSample = function() {
		var promi2 = restfactory.GET("/add-sample?sampletypeid="+this.sampletype.id)
		promi2.then(function(rest){
			if (rest.data.status=="ok") {
				$state.go('sample',{sampleID:rest.data.id})
			}
		},function(){
			console.log ("failure")
		})
		
	}
   
    //activation 
    this.loadTypes();
}  


angular.module('unidaplan').controller('newSampleController',['restfactory','$translate','$scope','$state',newSampleController]);

})();