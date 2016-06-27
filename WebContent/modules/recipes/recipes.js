(function(){
'use strict';

function recipesController(restfactory,$state,$stateParams,$translate,stypes,ptypes,avProcessTypeService,processService,sampleService){
	
	var thisController = this;
	
	this.processTypes = ptypes;
	
	this.sampleTypes = stypes;
		
	this.id = $stateParams.id;
		
	this.type = $stateParams.type || "process";
		
	this.strings = [];
		
	
	
//	this.keyUp = function(keyCode,newValue,parameter) {
//		if (keyCode === 13) {				// Return key pressed
//			if (type === 'process'){
//				this.addProcessRecipe();
//			} else {
//				this.addSampleRecipe();
//			}
//		}
//	}

	
	
	this.addRecipe = function() {
		var name;
		if (this.type === 'process'){
			name = {en: "new Process Recipe", de : "neues Prozessrezept"};
			var promise = processService.addProcessRecipe(name,thisController.processType.id);
		} else {
			name = {en: "new Sample Recipe", de : "neues Probenrezept"};
			var promise = sampleService.addSampleRecipe(name,thisController.processType.id);
		}
		promise.then(function(rest){
			if (rest.data.status === "ok") {
				console.log ("id:" + rest.data.id)
				if (thisController.type === 'process'){
					$state.go('processRecipe',{recipeID:rest.data.id})
				}else{
					$state.go('sampleRecipe',{recipeID:rest.data.id})
				}
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
			case "edit"   :  if (thisController.type === 'process'){
								$state.go('processRecipe',{processID : this.processType.id, recipeID:recipe.id})
							}else{
								$state.go('sampleRecipe',{  sampleID : this.processType.id, recipeID:recipe.id})
							};
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
	angular.forEach(stypes, function(stype){
		angular.forEach(stype.recipes, function(recipe){
			recipe.actions = [{
					action:"edit", 
					name: $translate.instant("edit")
				},{
					action:"delete",
					name: $translate.instant("delete")
				}];
		});
	});
	
	
	
	// prefill selector for processes
	if (ptypes){
		if ($stateParams.id) {
			for (var i = 0; i < ptypes.length ; i++){
				if (ptypes[i].id == $stateParams.id){
					this.processType = ptypes[i];
				}
			}
		} else {
			this.processType = ptypes[0];
		}
	}
	
	// prefill selector for samples
	if (stypes){
		if ($stateParams.id) {
			for (var i = 0; i < stypes.length ; i++){
				if (stypes[i].id == $stateParams.id){
					this.sampleType = stypes[i];
				}
			}
		} else {
			this.sampleType = stypes[0];
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
                              '$state','$stateParams','$translate','stypes','ptypes',
                              'avProcessTypeService','processService','sampleService',recipesController]);

})();