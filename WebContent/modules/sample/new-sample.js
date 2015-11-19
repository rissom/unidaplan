(function(){
'use strict';

function newSampleController(restfactory,$state,key2string,types){
	
	var thisController = this;
	
	this.sampletypes = types;
	
	this.strings = [];
			
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			this.addSample();
		}
	}

		
	this.addSample = function() {
		var promise = restfactory.POST("add-sample?sampletypeid="+this.sampletype.id)
		promise.then(function(rest){
			if (rest.data.status=="ok") {
				$state.go('sample',{sampleID:rest.data.id})
			}
		},function(){
			console.log ("failure")
		})		
	}
	
	// activate function (prefill selector)
	this.sampletype = types[0];
   
}  


angular.module('unidaplan').controller('newSampleController',['restfactory','$state','key2string','types',newSampleController]);

})();