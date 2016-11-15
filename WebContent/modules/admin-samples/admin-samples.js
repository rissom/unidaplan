(function(){
  'use strict';

function aSamplesController($state,$stateParams,$translate,restfactory,sampleService,types,languages){
  
	var thisController = this;
  
	this.languages = languages;
  
	this.lang1 = $translate.instant(languages[0].name);
  
	this.lang2 = $translate.instant(languages[1].name);
  
	this.types = types;
    
  
  
  	this.newSampleType = function(){
  		// add a new ParameterGroup to the database.
	 	var name = {};
	 	name[languages[0].key] = $translate.instant("new sampletype");
	 	name[languages[1].key] = "new sampletype";
	 	var position = 0;
	 	if (thisController.types){
	 		position = thisController.types.length + 1;
	 	}
	  	var newSampleType = { "name" : name };	  
	 	var promise = sampleService.addSampleType(newSampleType);
	 	promise.then(
	 		function(data) {
	 			$state.go("editSTParamGrps",{sampleTypeID:data.data.id, newSampletype:"true"}); // GOTO new sampletype
	 		},
		 	function(data) {
				console.log('error');
	 			console.log(data);
	 		}
	 	);
  	};
  	
  	
  	
  	this.addSampleType = function(){
  		var name = {};
  		name[languages[0].key] = this.newNameL1;
  		name[languages[1].key] = this.newNameL2;
  		var description = {};
  		description[languages[0].key] = this.newDescL1;
  		description[languages[1].key] = this.newDescL2; 	  
  		var newSampleType = {"name":name,"description":description};	  
  		var promise = sampleService.addSampleType(newSampleType);
  		promise.then(function(){ reload(); }, function(){ console.log("error"); })
    };

  
  
    this.performAction = function(sampleType,action){
	  	if (action.action === "edit"){
			$state.go( "editSTParamGrps", {sampleTypeID:sampleType.id} );
	  	}
	  	if (action.action === "delete" && sampleType.deletable){
	  		var promise=sampleService.deleteSampleType(sampleType.id);
	  		promise.then(function(){reload();},function(){console.log("error");})
	  	}
    };
  
  
  
    this.keyUp = function(keyCode) {
    	if (keyCode === 13) {				// Return key pressed
    		this.addSampleType();
    	}
		if (keyCode === 27) {		// Escape key pressed
			thisController.editmode = false;
		}
    };
  
  
  
  var reload = function() {
  	var current = $state.current;
  	var params = angular.copy($stateParams);
  	params.newSearch = false;
  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  };
 

};

angular.module('unidaplan').controller('aSamplesController', ['$state','$stateParams',
       '$translate','restfactory','sampleService','types','languages',aSamplesController]);

})();