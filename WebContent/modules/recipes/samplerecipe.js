(function(){
'use strict';

function sampleRecipeController(restfactory,$state,$stateParams,$translate,recipeData,stypes,users,groups,avSampleTypeService,sampleService,languages){
		
	var thisController = this;
	
	this.sampleTypes = stypes;
	
	this.recipes = stypes[0].recipes;
	
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
	
	this.id = recipeData.sampletype;
		
	
	this.changeOwner = function() {
		var promise = sampleService.changeOwner($stateParams.recipeID,thisController.owner.id);
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
		var promise = sampleService.saveSampleRecipeParameter($stateParams.recipeID,parameter);
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
			this.addsampleRecipe();
		}
	}
	

	
	this.keyUpN = function(keyCode, newName, language) {
		if (keyCode === 13) {				// Return key pressed
			var promise = sampleService.updateSampleRecipeName($stateParams.recipeID,newName, language);
			promise.then(function(){reload();},function(){console.log("error")});
		}
		if (keyCode===27) {		// Escape key pressed
			thisController.editFieldNL1 = false;
			thisController.editFieldNL2 = false;
		}
	}
	

	
	// activate function (prefill selector)
	if (stypes){
		if (stypes[0]){
			this.sampleType = stypes[0];
		}
	}
	
	
	
	var reload = function() {
		var current = $state.current;
		var params = angular.copy($stateParams);
		return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};
   
}  


angular.module('unidaplan').controller('sampleRecipeController',[
 'restfactory','$state','$stateParams',
  '$translate','recipeData','stypes','users','groups','avSampleTypeService',
  'sampleService','languages',sampleRecipeController]);

})();