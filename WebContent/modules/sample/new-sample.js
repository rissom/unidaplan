(function(){
'use strict';

function newSampleController(restfactory,$state,$translate,sampleService,key2string,types){
	
	var thisController = this;
	
	this.sampletypes = types;
	
	this.strings = [];
	
	// activate function
	angular.forEach(types, function(type){
		if (typeof(type.recipes) != "undefined") {
			type.recipes.unshift({
				namef : function(){ return ( "-- " + $translate.instant("without recipe") + " --" ) },
				id    : 0 
			});
		}
	}) 
			
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode === 13) {				// Return key pressed
			this.addSample();
		}
	}

		
	this.addSample = function() {
		var recipeID = 0;
		if (typeof(this.recipe) != "undefined" && typeof(this.recipe.id) != "undefined"){
			recipeID = this.recipe.id;
		}
		var promise = sampleService.addSample(this.sampletype.id, recipeID );
		promise.then(function(rest){
			if (rest.data.status == "ok") {
				$state.go('sample',{sampleID:rest.data.id})
			}
		},function(){
			console.log ("failure")
		})		
	}
	
	// activate function (prefill selector)
	this.sampletype = types[0];
   
}  


angular.module('unidaplan').controller('newSampleController',['restfactory','$state','$translate','sampleService','key2string','types',newSampleController]);

})();