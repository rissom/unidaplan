(function(){
  'use strict';

function parameterController($state,$stateParams,$translate,parameterService,restfactory,parameters,languages){
  
  var thisController=this;
  
  this.languages=languages;
  
  this.lang1=$translate.instant(languages[0].name);
  
  this.lang2=$translate.instant(languages[1].name);
  
  this.parameters=parameters;
  
  this.dataTypes=["integer","float","measurement","string","long string","chooser","timestamp","checkbox"];
  
  this.datatype=['none','integer','float','measurement','string',
                 'long string','chooser','timestamp','checkbox'];
  
  
  
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

	  var newParameter={"name":name,"unit":unit,"description":description,
			  		 "maxdigits":this.newMaxDigits,"datatype":this.newDataType};	  
	  
	  var promise = parameterService.addParameter(newParameter);
	  promise.then(function(){reload();},function(){console.log("error");})
  }
  
  
  var reload=function() {
	    var current = $state.current;
	    var params = angular.copy($stateParams);
	    return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  }
 
  
  
  this.deleteParameter = function(parameter){
	  var promise = parameterService.deleteParameter(parameter.id);
	  promise.then(function(){reload();},function(){console.log("error");});
  };
  
  
  
  this.assign=function(){
	  var samples2assign={samples:this.process.samples, id:processData.id};
	  var promise = restfactory.POST("add-sample-to-process",samples2assign);
  }
 
};

angular.module('unidaplan').controller('parameterController', ['$state','$stateParams','$translate',
    'parameterService','restfactory','parameters','languages',parameterController]);

})();