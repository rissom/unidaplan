(function(){
'use strict';

function processRecipeController(restfactory,recipeData,$state,$stateParams,$translate,ptypes,avProcessTypeService,processService,languages,users){
	
	var thisController = this;
	
	this.processTypes = ptypes;
	
	this.recipes = ptypes[0].recipes;
	
	this.strings = [];
	
	this.recipe = recipeData;
	
	this.parametergroups = recipeData.parametergroups
	
	this.languages = languages;
	  
	this.nameL1 = recipeData.nameLang(languages[0].key);
	  
	this.newNameL1 = recipeData.nameLang(languages[0].key);
	  
	this.nameL2 = recipeData.nameLang(languages[1].key);

	this.newNameL2 = recipeData.nameLang(languages[1].key);
	    
	this.lang1 = $translate.instant(languages[0].name);
	  
	this.lang2 = $translate.instant(languages[1].name);
	  
	this.lang1key = $translate.instant(languages[0].key);
	  
	this.lang2key = $translate.instant(languages[1].key);
	  
	this.editFieldNL1 = false;
	  
	this.editFieldNL2 = false;
	
	this.users = users;
	
	this.owner = users.filter(function(user){return user.id == recipeData.owner})[0];
		
	
	this.changeOwner = function() {
		var promise = processService.changeOwner($stateParams.recipeID,thisController.owner.id);
		promise.then(function(){
			reload();
		},function(){
			console.log("error");
			thisController.editOwner = false;
		});
	};
	
	
	  
	this.edit = function(field){
		thisController.editFieldNL1 = (field == "NL1");
		thisController.editFieldNL2 = (field == "NL2");
		thisController.editOwner = (field == "owner");
		thisController.newNameL1 = thisController.nameL1;
		thisController.newNameL2 = thisController.nameL2;
		thisController.newOwner = thisController.nameL2;


	};
	  
	
	
 	this.saveParameter = function(parameter) {
		var promise = processService.saveProcessRecipeParameter($stateParams.recipeID,parameter);
		promise.then(
			function(data) {
				reload();
			},
			function(data) {
				console.log('error');
				console.log(data);	
				reload();
			}
		);
	};
	
	
	
	this.keyUp = function(keyCode, newValue, parameter) {
		if (keyCode === 13) {				// Return key pressed
			this.addProcessRecipe();
		}
	}
	

	
	this.keyUpN = function(keyCode, newName, language) {
		if (keyCode === 13) {				// Return key pressed
			var promise = processService.updateProcessRecipeName($stateParams.recipeID,newName, language);
			promise.then(function(){reload();},function(){console.log("error")});
		}
		if (keyCode===27) {		// Escape key pressed
			thisController.editFieldNL1 = false;
			thisController.editFieldNL2 = false;
		}
	}
	

	
	// activate function (prefill selector)
	if (ptypes){
		if (ptypes[0]){
			this.processType = ptypes[0];
		}
	}
	
	
	
	var reload = function() {
		var current = $state.current;
		var params = angular.copy($stateParams);
		return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};
   
}  


angular.module('unidaplan').controller('processRecipeController',['restfactory','recipeData','$state','$stateParams',
                                                                  '$translate','ptypes','avProcessTypeService',
                                                                  'processService','languages','users',processRecipeController]);

})();