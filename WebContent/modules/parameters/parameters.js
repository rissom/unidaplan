(function(){
  'use strict';

function parameterController($state,$stateParams,avParameterService,restfactory,parameters){
  
  var thisController=this;
  	
  this.parameters=parameters;
  
  this.test="hallo";
  
  var reload=function() {
	    var current = $state.current;
	    var params = angular.copy($stateParams);
	    return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  }
	
 
  
  this.deleteParameter = function(parameter){
	  return parameterService.deleteParameter(parameter.id);
  }
  
  
  
  this.assign=function(){
	  var samples2assign={samples:this.process.samples, id:processData.id};
	  var promise = restfactory.POST("add-sample-to-process",samples2assign);
  }
 
  
};

angular.module('unidaplan').controller('parameterController', ['$state','$stateParams','avParameterService','restfactory','parameters',parameterController]);

})();