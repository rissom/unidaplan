(function(){
  'use strict';

function parameterController($scope,$state,$stateParams,$translate,parameterService,restfactory,parameters,languages){
  
  var thisController=this;
  
  this.languages=languages;
  
  this.lang1=$translate.instant(languages[0].name);
  
  this.lang2=$translate.instant(languages[1].name);
  
  this.lang1Key=languages[0].key;
  
  this.lang2Key=languages[1].key;
  
  this.parameters=parameters;
  
  this.dataTypes=["integer","float","measurement","string","long string","chooser","date","checkbox","timestamp","URL","somethingelse"];
  
  
  this.newParameter=function(){
	  this.editmode=true;
  }
  
  
  
  this.addParameter=function(){
	  var name={};
	  name[languages[0].key]=this.newNameL1;
	  name[languages[1].key]=this.newNameL2;
	  var unit={};
	  unit[languages[0].key]=this.newUnitL1;
	  unit[languages[1].key]=this.newUnitL2; 
	  var description={};
	  description[languages[0].key]=this.newDescL1;
	  description[languages[1].key]=this.newDescL2; 	  

	  var newParameter={"name":name,"description":description,"unit":unit};
	  newParameter.datatype=thisController.newDataType;
	  newParameter.maxdigits=thisController.newMaxDigits;
	  
	  var promise = parameterService.addParameter(newParameter);
	  promise.then(function(){reload();},function(){console.log("error");})
  }
  
  
  
  this.deleteParameter = function(parameter){
	  var promise = parameterService.deleteParameter(parameter.id);
	  promise.then(function(){reload();},function(){console.log("error");});
  };
  

  
  this.keyUp = function(keyCode) {
	if (keyCode===13) {				// Return key pressed
		if ($scope.parameterForm.$valid){
			thisController.addParameter(parameter);
		}
	}
	if (keyCode===27) {		// Escape key pressed
		parameter.editing=false;
	}
  };
  
  var reload=function() {
  	var current = $state.current;
  	var params = angular.copy($stateParams);
  	params.newSearch=false;
  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  };
  
};

angular.module('unidaplan').controller('parameterController', ['$scope','$state','$stateParams','$translate',
    'parameterService','restfactory','parameters','languages',parameterController]);

})();