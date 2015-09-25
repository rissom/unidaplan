(function(){
  'use strict';

function editProcessController($state,$stateParams,$translate,restfactory,processService,parameters,languages){
  
  var thisController=this;
  
  this.languages=languages;
  
  this.lang1=$translate.instant(languages[0].name);
  
  this.lang2=$translate.instant(languages[1].name);
  
  this.parameters=parameters;
    
  
  this.newParameter=function(){
	  this.editmode=true;
  }
  
	
  this.getActions = function(user){
	return [$translate.instant("edit"),$translate.instant("delete")];
  }
	
  
  this.performAction = function(index,process){
	  	if (index==1){
	  		var promise;
	  		promise.then(function(){reload();},function(){console.log("error");})
	  	}
  }
  
  
  
  var reload=function() {
	    var current = $state.current;
	    var params = angular.copy($stateParams);
	    return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  }
  
  
  
  this.addParameter=function(){
  } 

};

angular.module('unidaplan').controller('editProcessController', ['$state','$stateParams','$translate','restfactory','processService','parameters','languages',editProcessController]);

})();