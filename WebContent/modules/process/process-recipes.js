(function(){
'use strict';

function processRecipesController(restfactory,$state,ptypes,avProcessTypeService,processService){
	
	var thisController = this;
	
	this.processTypes = ptypes;
	
	this.strings = [];
	
	this.hallo = 'Hallo!';			
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			this.addProcessRecipe();
		}
	}

		
	this.addProcessRecipe = function() {
		
		var promise = processService.addProcessRecipe(this.processType.id);
		promise.then(function(rest){
			if (rest.data.status=="ok") {
				$state.go('processRecipe',{processRecipeID:rest.data.id})
			}
		},function(){
			console.log ("failure")
		})		
	}
	
	// activate function (prefill selector)
	if (ptypes){
		if (ptypes[0]){
			this.processType = ptypes[0];
		}
	}
   
}  


angular.module('unidaplan').controller('processRecipesController',['restfactory','$state','ptypes',
                              'avProcessTypeService',processRecipesController]);

})();