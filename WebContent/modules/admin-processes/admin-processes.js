(function(){
  'use strict';

function aProcessesController($state,$stateParams,$translate,restfactory,processService,ptypes,languages){
  
	var thisController=this;
  
	this.languages=languages;
	  
	this.lang1=$translate.instant(languages[0].name);
	  
	this.lang2=$translate.instant(languages[1].name);
  
	this.ptypes=ptypes;
    
  
  
	this.newProcessType=function(){
		this.editmode=true;
	};
  
  
  
	this.performAction = function(process,action){
		if (action.action==="edit"){
			$state.go("editPtParamGrps",{processTypeID:process.id});
		}
		if (action.action==="duplicate"){
	  	  	var promise=processService.duplicateProcessType(process.id);
	  	  	promise.then(function(){reload();},function(){console.log("error");});
		}
		if (action.action==="delete"){
	  	  	var promise2=processService.deleteProcessType(process.id);
	  	  	promise2.then(function(){reload();},function(){console.log("error");});
		}
	};
  
  
  
	this.keyUp = function(keyCode) {
		if (keyCode===13) {				// Return key pressed
			this.addProcessType();
		}
		if (keyCode===27) {		// Escape key pressed
			this.editmode=false;
		}
	};
  
  
  
	this.addProcessType=function(){
		var name={};
		name[languages[0].key]=this.newNameL1;
		name[languages[1].key]=this.newNameL2;
		var description={};
		description[languages[0].key]=this.newDescL1;
		description[languages[1].key]=this.newDescL2; 	  
		var newProcessType={"name":name,"description":description};	  
		var promise = processService.addProcessType(newProcessType);
		promise.then(function(){ reload();},function(){console.log("error");});
	};
	
	
	
	var reload=function() {
		var current = $state.current;
		var params = angular.copy($stateParams);
		return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};

}

angular.module('unidaplan').controller('aProcessesController', ['$state','$stateParams','$translate','restfactory','processService','ptypes','languages',aProcessesController]);

})();