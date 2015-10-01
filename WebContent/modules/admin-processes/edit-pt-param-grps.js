(function(){
  'use strict';

function editPtParamGrpsController($state,$stateParams,$translate,restfactory,processService,processType,languages){
  
  var thisController=this;
    
  this.parametergrps=processType.parametergrps;
  
  this.strings=processType.strings;
  
  this.languages=languages;
  
  this.newNameL1 = processType.nameLang(languages[0].key);

  this.newNameL2 = processType.nameLang(languages[1].key);

  this.newDescL1 = processType.descLang(languages[0].key);

  this.newDescL2 = processType.descLang(languages[1].key);
  
  this.lang1=$translate.instant(languages[0].name);
  
  this.lang2=$translate.instant(languages[1].name);
    
  var thisController=this;
  
  this.getActions = function(user){
		return [$translate.instant("edit"),$translate.instant("delete")];
	  }
  
  this.getGrpName=function(grp,lang){
	  key2string.key2stringWithLangStrict(grp.name,thisController.strings,lang)
  }
  
  
  
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

angular.module('unidaplan').controller('editPtParamGrpsController', ['$state','$stateParams','$translate',
       'restfactory','processService','processType','languages',editPtParamGrpsController]);

})();