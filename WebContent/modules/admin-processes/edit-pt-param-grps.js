(function(){
  'use strict';

function editPtParamGrpsController($state,$stateParams,$translate,$scope,$uibModal,avParameters,restfactory,processService,processType,languages,avProcessTypeService){
  
	var thisController = this;
    
	this.parametergrps = processType.parametergrps.sort(
		function(a,b){
			return a.pos-b.pos;
		});
  
	this.samplerparams = processType.samplerparams.sort(
		  function(a,b){
			  return a.position-b.position;
		  });
  
	this.strings = processType.strings;
  
	this.languages = languages;
  
	this.NameL1 = processType.nameLang(languages[0].key);
  
	this.newNameL1 = processType.nameLang(languages[0].key);
  
	this.NameL2 = processType.nameLang(languages[1].key);

	this.newNameL2 = processType.nameLang(languages[1].key);
  
	this.DescL1 = processType.descLang(languages[0].key);

	this.newDescL1 = processType.descLang(languages[0].key);
  
	this.DescL2 = processType.descLang(languages[1].key);
  
	this.newDescL2 = processType.descLang(languages[1].key);
    
	this.lang = function(l){return $translate.instant(languages[l].name)};
       
	this.editNL1 = $stateParams.newProcesstype === "true";

  
  
  
  this.addSRParameter = function(){
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
    		  var promise=avProcessTypeService.addProcesstypeSRParameters($stateParams.processTypeID,result.chosen);
    		  promise.then(function(){reload();});		    	  
    	  }
	    }, function () {
	      console.log('Strange Error: Modal dismissed at: ' + new Date());
	    });
  }
  
  
  
  this.changeField = function(field){
	  thisController.editNL1 = false;
		thisController.editNL2 = false;
		thisController.editDL1 = false;
		thisController.editDL2 = false;
		var value="";
		var fieldType="name";
		var lang="";
		switch (field){
			case "NL1": fieldType="name"; value=thisController.newNameL1; lang=languages[0].key; break;
			case "NL2": fieldType="name"; value=thisController.newNameL2; lang=languages[1].key;break;
			case "DL1": fieldType="description"; value=thisController.newDescL1; lang=languages[0].key; break;
			case "DL2": fieldType="description"; value=thisController.newDescL2; lang=languages[1].key; break;
			default: console.log("no field given!");
		}
		var promise = avProcessTypeService.updateProcessTypeData(processType.id,fieldType,value,lang);
		promise.then(function(data) {
				reload();
			 },
			 function(data) {
				console.log('error');
				console.log(data);
			 }
			);
  };
  
  
  
  this.down = function(index){
	  var id1=thisController.parametergrps[index].id;
	  var id2=thisController.parametergrps[index+1].id;
	  var pos1=thisController.parametergrps[index+1].pos;
	  var pos2=thisController.parametergrps[index].pos;
	  var promise = avProcessTypeService.exPosPTParamGrp(id1,pos1,id2,pos2);
	  promise.then(function(){reload()},function(){console.log("error")})
  };
  
  
  
  this.downSR = function(index){
	  var id1=thisController.samplerparams[index].id;
	  var id2=thisController.samplerparams[index+1].id;
	  var pos1=thisController.samplerparams[index+1].position;
	  var pos2=thisController.samplerparams[index].position;
	  var promise = avProcessTypeService.exPosPTSRParams(id1,pos1,id2,pos2);
	  promise.then(function(){reload()},function(){console.log("error")})
  };

  
  
  this.edit = function(field){
	  thisController.editNL1 = (field=="NL1");
	  thisController.editNL2 = (field=="NL2");
	  thisController.editDL1 = (field=="DL1");
	  thisController.editDL2 = (field=="DL2");
  };
	
  
  
  this.getGrpName = function(grp,lang){
	  key2string.key2stringWithLangStrict(grp.name,thisController.strings,lang);
  };
  
  

  this.keyUp = function(keyCode,field) {
	if (keyCode === 13) {				// Return key pressed
		this.changeField(field);
	}
	if (keyCode === 27) {		// Escape key pressed
		thisController.editNL1 = false;
		thisController.editNL2 = false;
		thisController.editDL1 = false;
		thisController.editDL2 = false;
		thisController.newNameL1 = processType.nameLang(languages[0].key);
		thisController.newNameL2 = processType.nameLang(languages[1].key);
		thisController.newDescL1 = processType.descLang(languages[0].key);
		thisController.newDescL2 = processType.descLang(languages[1].key);
	}
  };

  
  
  this.keyUpPG = function(keyCode) {
	  if (keyCode === 13) {				// Return key pressed
		  this.addParameterGroup();
	  }
	  if (keyCode === 27) {		// Escape key pressed
		  this.editmode=false;
	  }
  };
  
  
  
  this.newParameterGroup = function(){
	 	 // add a new ParameterGroup to the database.
	 	var name = {};
	 	name[languages[0].key] = $translate.instant("new parametergroup");
	 	name[languages[1].key] = "new Parametergroup";
	 	var position = 0;
	 	if (thisController.parametergrps){
	 		position = thisController.parametergrps.length + 1;
	 	}
	 	var promise = avProcessTypeService.addPTParameterGrp(processType.id,position,name);
	 	promise.then(function(data) {
	 			$state.go("editPtParams",{paramGrpID:data.data.id, newGrp:"true"}); // GOTO new parametergroup
	 		},
 		 	function(data) {
 				console.log('error');
	 			console.log(data);
	 		}
	 	);
  };
 
  
  
  this.performAction = function(parametergrp,action){
	  if (action.action === "edit" && !action.disabled) {
		  $state.go("editPtParams",{"paramGrpID":parametergrp.id})
	  }
	  if (action.action == "delete" && !action.disabled) {
		  var promise = avProcessTypeService.deletePTParameterGrp(parametergrp.id);
		  promise.then(function(){reload()},function(){console.log("error")});
	  }
  };
  
  
  
  this.performSRAction = function(sparam,action){
	  if (action.action === "edit" && !action.disabled) {
		  $state.go("editSinglePOParameter",{parameterID:sparam.id})
	  }
	  if (action.action == "delete" && !action.disabled) {
		  console.log("sparam"+sparam)
		  var promise = avProcessTypeService.deletePTSRParameter(sparam.id);
		  promise.then(function(){reload()},function(){console.log("error")});
	  }
  };
  

  var reload = function() {
	    var current = $state.current;
	    var params = angular.copy($stateParams);
	    return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  };

  

  this.up = function(index){
	  var id1=thisController.parametergrps[index-1].id;
	  var id2=thisController.parametergrps[index].id;
	  var pos1=thisController.parametergrps[index].pos;
	  var pos2=thisController.parametergrps[index-1].pos;
	  var promise = avProcessTypeService.exPosPTParamGrp(id1,pos1,id2,pos2);
	  promise.then(function(){reload()},function(){console.log("error")})
  };
  
  
  
  this.upSR = function(index){
	  var id1=thisController.samplerparams[index-1].id;
	  var id2=thisController.samplerparams[index].id;
	  var pos1=thisController.samplerparams[index].position;
	  var pos2=thisController.samplerparams[index-1].position;
	  var promise = avProcessTypeService.exPosPTSRParams(id1,pos1,id2,pos2);
	  promise.then(function(){reload()},function(){console.log("error")})
  };
  
};

angular.module('unidaplan').controller('editPtParamGrpsController', [
       '$state','$stateParams','$translate','$scope','$uibModal','avParameters',
       'restfactory','processService','processType','languages','avProcessTypeService',
       editPtParamGrpsController]);

})();