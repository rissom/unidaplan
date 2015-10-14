(function(){
  'use strict';

function editPtParamsController($state,$modal,$stateParams,$translate,avParameters,restfactory,processService,parameterGrp,languages,avProcessTypeService){
  
  var thisController=this;
    
  this.parameters=parameterGrp.parameters.sort(function(a,b){return a.pos-b.pos});
  
  this.strings=parameterGrp.strings;
  
  this.processtype=parameterGrp.processtype;
  
  this.languages=languages;
  
  this.NameL1 = parameterGrp.nameLang(languages[0].key);
  
  this.newNameL1 = parameterGrp.nameLang(languages[0].key);
  
  this.NameL2 = parameterGrp.nameLang(languages[1].key);

  this.newNameL2 = parameterGrp.nameLang(languages[1].key);
    
  this.lang1=$translate.instant(languages[0].name);
  
  this.lang2=$translate.instant(languages[1].name);
    
  var thisController=this;
  
  
  
  this.getGrpName=function(grp,lang){
	  key2string.key2stringWithLangStrict(grp.name,thisController.strings,lang)
  }
  
  
  
  this.newParameter=function(){
	  this.editmode=true;
  }
  
  
  
  this.edit = function(field){
	  thisController.editNL1 = (field=="NL1");
	  thisController.editNL2 = (field=="NL2");
	  thisController.editDL1 = (field=="DL1");
	  thisController.editDL2 = (field=="DL2");
  }
	
  
  
  var reload=function() {
	    var current = $state.current;
	    var params = angular.copy($stateParams);
	    return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  }
  
  
  
  this.getActions=function(parametergrp){
	  return [$translate.instant("edit"),$translate.instant("delete")]
  }
  
  
  
  this.performAction=function(index,parameter){
	  console.log("parameter: ",parameter);
	  console.log("index: ",index);
	  if (index==1) {
		  var promise = avProcessTypeService.deletePTParameter(parameter.id);
		  promise.then(function(){reload()},function(){console.log("error")});
	  }
  }
  
  
	this.addParameter = function () {
		  var modalInstance = $modal.open({
		    animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser.html',
		    controller: 'modalParameterChoser as mParameterChoserCtrl',
//		    size: 'sm',
		    resolve: {
		    	mode		  	 : function(){return 'immediate'; },
		    	avParameters     : function(){return avParameters; },
//		    	chosenParameters : function(){return thisController.parameters.map(function(p){return p.definition}); }
		    	chosenParameters : function(){return []; }
			}
		  });
		  
		  modalInstance.result.then(function (result) {  // get the new Parameterlist + Info if it has changed from Modal. 
		      if (result.changed) {
		    	  if (result.chosen.length>0){
		    		  var promise=avProcessTypeService.AddProcesstypePGParameters(thisController.processtype,
		    				  parameterGrp.id,result.chosen);
		    		  promise.then(function(){reload();});		    	  
		    	  }
		      }
		    }, function () {
		      console.log('Strange Error: Modal dismissed at: ' + new Date());
		    });
	  };
	 
	  
  
  
  
  this.keyUp = function(keyCode,field) {
	if (keyCode===13) {				// Return key pressed
		
		
	}
	if (keyCode===27) {		// Escape key pressed
	
	}
  }

  
  
  this.down=function(index){  // exchange two parameter positions
	  var newPositions=[];
	  newPositions.push({"id":thisController.parameters[index].id,"position":thisController.parameters[index+1].pos});
	  newPositions.push({"id":thisController.parameters[index+1].id,"position":thisController.parameters[index].pos});
	  var promise = avProcessTypeService.changeOrderPTParameters(newPositions);
	  console.log("newPositions: ",newPositions);
	  promise.then(function(){reload()},function(){console.log("error")})
  }

  
  
  this.up=function(index){  // exchange two parameter positions
	  var newPositions=[];
	  newPositions.push({"id":thisController.parameters[index-1].id,"position":thisController.parameters[index].pos});
	  newPositions.push({"id":thisController.parameters[index].id,"position":thisController.parameters[index-1].pos});
	  var promise = avProcessTypeService.changeOrderPTParameters(newPositions);
	  promise.then(function(){reload()},function(){console.log("error")})
  }
  


};

angular.module('unidaplan').controller('editPtParamsController', ['$state','$modal','$stateParams','$translate',
       'avParameters','restfactory','processService','parameterGrp','languages','avProcessTypeService',editPtParamsController]);

})();