(function(){
  'use strict';

function aProcessesController($state,$stateParams,$translate,restfactory,processService,ptypes,languages){
  
	var thisController = this;
  
	this.languages = languages;
	  
	this.lang = function(l){return $translate.instant(languages[l].name)};
  
	this.ptypes = ptypes;
    
  
  
	this.newProcessType = function(){
  		// add a new processtype to the database.
	 	var name = {};
	 	name[languages[0].key] = $translate.instant("new processtype");
	 	name[languages[1].key] = "new processtype";
	 	var position = 0;
	 	if (thisController.types){
	 		position = thisController.ptypes.length + 1;
	 	}
	  	var newProcessType = { "name" : name };
	 	var promise = processService.addProcessType(newProcessType);
	 	promise.then(
	 		function(data) {
	 			$state.go("editPtParamGrps",{processTypeID:data.data.id, newProcesstype:"true"}); 
	 		},
		 	function(data) {
				console.log('error');
	 			console.log(data);
	 		}
	 	);
  	};
  	
  
  
	this.performAction = function(process,action){
		if (action.action === "edit"){
			$state.go("editPtParamGrps",{processTypeID:process.id});
		}
		if (action.action === "duplicate"){
	  	  	var promise = processService.duplicateProcessType(process.id);
	  	  	promise.then(function(){reload();},function(){console.log("error");});
		}
		if (action.action === "delete" && process.deletable){
	  	  	var promise2 = processService.deleteProcessType(process.id);
	  	  	promise2.then(function(){reload();},function(){console.log("error");});
		}
	};
  
  
  
	this.keyUp = function(keyCode) {
		if (keyCode === 13) {				// Return key pressed
			this.addProcessType();
		}
		if (keyCode === 27) {		// Escape key pressed
			this.editmode=false;
		}
	};
	
	
	
	var reload = function() {
		var current = $state.current;
		var params = angular.copy($stateParams);
		return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};

}

angular.module('unidaplan').controller('aProcessesController', ['$state','$stateParams','$translate','restfactory','processService','ptypes','languages',aProcessesController]);

})();