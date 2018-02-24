(function(){
  'use strict';

function editPtParamsController($state,$uibModal,$stateParams,$translate,avParameters,ptypes,restfactory,processService,parameterGrp,languages,avProcessTypeService){
  
  var thisController = this;
    
  var activeParameter = {};
    
  this.parameters = parameterGrp.parameters.sort(function(a,b){
	  return a.pos-b.pos;
  });
  
  this.strings = parameterGrp.strings;
  
  this.processtype = parameterGrp.processtype;
  
  this.languages = languages;
      
  this.lang1 = $translate.instant(languages[0].name);
  
  this.lang2 = $translate.instant(languages[1].name);
  
  this.lang1key = languages[0].key;
  
  this.lang2key = languages[1].key;
    
  this.nL1 = { data    : { value: parameterGrp.nameLang(languages[0].key) },
               editing : ($stateParams.newGrp === 'true'),
               lang    : languages[0].key
             }

  this.nL2 = { data     : { value: parameterGrp.nameLang(languages[1].key) },
               editing  : false,
               lang     : languages[1].key
             }
  
  this.av = avParameters;
    
  

  this.addParameter = function () {
	  var modalInstance = $uibModal.open({
	    animation: false,
	    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser.html',
	    controller: 'modalParameterChoser as mParameterChoserCtrl',
	    resolve: {
	    	mode		   : function(){return 'immediate'; },
	    	avParameters   : function(){return avParameters; },
	    	parameters     : function(){return []; }
		}
	  });
	  
	  modalInstance.result.then(function (result) {  // get the new Parameterlist + Info if it has changed from Modal.  
    	  if (result.chosen.length>0){
    		  var promise = avProcessTypeService.AddProcesstypePGParameters(thisController.processtype,
    				  parameterGrp.id,result.chosen);
    		  promise.then(reload,error);		    	  
    	  }
	    }, function () {
	      console.log('Strange Error: Modal dismissed at: ' + new Date());
	    });
  };
  
  

  this.down = function(index){  // exchange two parameter positions
	  var newPositions = [];
	  newPositions.push({"id":thisController.parameters[index].id,"position":thisController.parameters[index+1].pos});
	  newPositions.push({"id":thisController.parameters[index+1].id,"position":thisController.parameters[index].pos});
	  var promise = avProcessTypeService.changeOrderPTParameters(newPositions);
      promise.then(reload,error)
  };

  
  
//  this.getGrpName = function(grp,lang){
//	  key2string.key2stringWithLangStrict(grp.name,thisController.strings,lang);
//  };
  
  
  
//  this.getPTName = function(ptypeid){
//	  return avProcessTypeService.getProcessType({processtype:thisController.processtype},ptypes);
//  }


  
  this.keyUpParameter = function(keyCode,parameter) {
	  if (keyCode === 13) {				// Return key pressed
		  console.log("Return pressed");
		  thisController.submitParameter();
	  }
	  if (keyCode === 27) {		// Escape key pressed
		  parameter.editNL1=false;
		  parameter.editNL2=false;
	  }
  };
  
  
  
  this.performAction = function(parameter,action){
	  if (action.action === "delete" && !action.disabled) {
		  var promise = avProcessTypeService.deletePTParameter(parameter.id);
		  promise.then(reload,error);
	  }
	  if (action.action === "edit") {
			$state.go('editSinglePTParameter',{parameterID:parameter.id});
	  }
  };
  
  
  
  this.setHidden = function(parameter){
	  var tempParameter = { parameterid : parameter.id,
			  			    hidden      : parameter.hidden};
	  var promise = avProcessTypeService.updateParameter(tempParameter);
      promise.then(reload,error)
  };
  
  
  
  this.setCompulsory = function(parameter){
	  var tempParameter = { parameterid : parameter.id,
			  		        compulsory  : parameter.compulsory };
	  var promise = avProcessTypeService.updateParameter(tempParameter);
      promise.then(reload,error)
  };
  
  
  
  this.updateparametername = function(p){
      p.editing = false;
	  var tempParameter = { parameterid:p.id, name:{} };	  
	  tempParameter.name[p.lang] = p.data.value;
	  var promise = avProcessTypeService.updateParameter(tempParameter);
      promise.then(reload,error)
  };
  
  
  
  this.updateParamGrpName = function(p) {
      var promise = avProcessTypeService.updateParamGrp(p.data.value, p.lang, parameterGrp.id);
      promise.then(reload,error);
  };
    
 
  
  this.up = function(index){  // exchange two parameter positions
	  var newPositions = [];
	  newPositions.push({"id":thisController.parameters[index-1].id,"position":thisController.parameters[index].pos});
	  newPositions.push({"id":thisController.parameters[index].id,"position":thisController.parameters[index-1].pos});
	  var promise = avProcessTypeService.changeOrderPTParameters(newPositions);
	  promise.then(reload,error)
  };
  
  
  
  var reload = function() {
	  var current = $state.current;
	  var params = angular.copy($stateParams);
	  params.newGrp = false;
	  return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  };
  
  
  
  var error = function() {
      console.error("error");
  };
};

angular.module('unidaplan').controller('editPtParamsController', ['$state','$uibModal','$stateParams','$translate',
       'avParameters','ptypes','restfactory','processService','parameterGrp','languages','avProcessTypeService',editPtParamsController]);

})();