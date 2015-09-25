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
  }
  
	
  this.getActions = function(user){
	return [$translate.instant("edit"),$translate.instant("duplicate"),$translate.instant("delete")];
  }
	
  
  this.performAction = function(index,process){
	  if (index==0){
		  $state.go("editParamType",{processTypeID:1});
	  }
	  if (index==1){
	  	  var promise=processService.duplicateProcessType(process.id);
	  	  promise.then(function(){reload();},function(){console.log("error");})
	  }
	  if (index==2){
	  	  var promise=processService.deleteProcessType(process.id);
	  	  promise.then(function(){reload();},function(){console.log("error");})
	  }
  }
  
  
  
  var reload=function() {
	    var current = $state.current;
	    var params = angular.copy($stateParams);
	    return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  }
  
  
  
  this.addProcessType=function(){
	  var name={};
	  name[languages[0].key]=this.newNameL1;
	  name[languages[1].key]=this.newNameL2;
	  var description={};
	  description[languages[0].key]=this.newDescL1;
	  description[languages[1].key]=this.newDescL2; 	  
	  var newProcessType={"name":name,"description":description};	  
	  var promise = processService.addProcessType(newProcessType);
	  promise.then(function(){ reload();},function(){console.log("error");})
  } 

};

angular.module('unidaplan').controller('aProcessesController', ['$state','$stateParams','$translate','restfactory','processService','ptypes','languages',aProcessesController]);

})();