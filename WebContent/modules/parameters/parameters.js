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
	  var name={};
	  name[languages[0].key]=$translate.instant('new Name');
	  name[languages[1].key]='new Name';
	  var unit={};
	  unit[languages[0].key]=$translate.instant('unit');
	  unit[languages[1].key]='unit'; 
	  var description={};
	  description[languages[0].key]=$translate.instant('description');
	  description[languages[1].key]='description'; 	  

	  var newParameter={name:name,description:description,unit:unit};
	  newParameter.datatype='integer';
	  
	  var promise = parameterService.addParameter(newParameter);
	  promise.then(function(){reload();},function(){console.log("error");})
  }
  
  

  this.performAction=function(parameter,action){
	  if (action.action==="edit") {
		  $state.go("editParameter",{parameterID:parameter.id})
	  }
	  if (action.action==="delete"  && !action.disabled) {
		  var promise = parameterService.deleteParameter(parameter.id);
		  promise.then(function(){reload();},function(){console.log("error");});
	  }
  };
  
  

  
  this.keyUp = function(keyCode) {
	if (keyCode===13) {				// Return key pressed
		if ($scope.parameterForm.$valid){
			thisController.addParameter();
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