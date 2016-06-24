(function(){
'use strict';

function recipesController(restfactory,$state,$stateParams,$translate,ptypes,avProcessTypeService,processService){
	
	var thisController = this;
	
	this.processTypes = ptypes;
	
	this.id = $stateParams.id;
	
	this.recipes = ptypes[0].recipes;
	
	this.type = $stateParams.type || "process";
		
	this.strings = [];
		
	
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode === 13) {				// Return key pressed
			this.addProcessRecipe();
		}
	}

	
	
	this.addRecipe = function() {
		var name = {en: "new Process Recipe", de : "neues Rezept"};
		console.log ("pt:",thisController.processType);
		var promise = processService.addProcessRecipe(name,thisController.processType.id);
		promise.then(function(rest){
			if (rest.data.status === "ok") {
				console.log ("id:" + rest.data.id)
				$state.go('processRecipe',{recipeID:rest.data.id})
			}
		},function(){
			console.log ("failure")
		})		
	}
	
	
	
	this.deleteRecipe = function(recipeID){
		var promise = processService.deleteRecipe(recipeID);
		promise.then(function(){reload()});
	}
	
	
	
	this.performAction = function(action,recipe){
		switch (action.action){
			case "edit"   :	$state.go('processRecipe',
								{ processID : this.processType.id,
								  recipeID  : recipe.id});
			break;
			case "delete" :	this.deleteRecipe(recipe.id); break;
		}
	};
	
	
	
	// activate function

	//define actions
	angular.forEach(ptypes, function(ptype){
		angular.forEach(ptype.recipes, function(recipe){
			recipe.actions = [{
					action:"edit", 
					name: $translate.instant("edit")
				},{
					action:"delete",
					name: $translate.instant("delete")
				}];
		});
	});
	
	
	
	// prefill selector
	if (ptypes){
		if ($stateParams.id) {
			for (var i = 0; i<ptypes.length ; i++){
				if (ptypes[i].id == $stateParams.id){
					this.processType = ptypes[i];
				}
			}
		} else {
			this.processType = ptypes[0];
		}
	}
	
	
	  var reload = function() {
		  var current = $state.current;
	      var params = angular.copy($stateParams);
	      params.newSearch = false;
	      return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	  };
	
}  


angular.module('unidaplan').controller('recipesController',['restfactory',
                              '$state','$stateParams','$translate','ptypes',
                              'avProcessTypeService','processService',recipesController]);

})();