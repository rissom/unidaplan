(function(){
  'use strict';

function editPtParamGrpsController($state,$stateParams,$translate,$scope,restfactory,processService,processType,languages,avProcessTypeService){
  
  var thisController=this;
    
  this.parametergrps=processType.parametergrps.sort(function(a,b){return a.pos-b.pos});
  
  this.strings=processType.strings;
  
  this.languages=languages;
  
  this.NameL1 = processType.nameLang(languages[0].key);
  
  this.newNameL1 = processType.nameLang(languages[0].key);
  
  this.NameL2 = processType.nameLang(languages[1].key);

  this.newNameL2 = processType.nameLang(languages[1].key);
  
  this.DescL1 = processType.descLang(languages[0].key);

  this.newDescL1 = processType.descLang(languages[0].key);
  
  this.DescL2 = processType.descLang(languages[1].key);
  
  this.newDescL2 = processType.descLang(languages[1].key);
    
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
  
  
  
  this.performAction=function(index,parametergrp){
	  console.log("group: ",parametergrp);
	  console.log("index: ",index);
	  if (index==1) {
		  var promise = avProcessTypeService.deletePTParameterGrp(parametergrp.id);
		  promise.then(function(){reload()},function(){console.log("error")});
	  }
  }
  
  
  
  this.keyUp = function(keyCode,field) {
	if (keyCode===13) {				// Return key pressed
		thisController.editNL1 = false;
		thisController.editNL2 = false;
		thisController.editDL1 = false;
		thisController.editDL2 = false;
		var value="";
		var fieldType="name";
		console.log("field:",field);
		switch (field){
			case "NL1": fieldType="name"; value=thisController.newNameL1; break;
			case "NL2": fieldType="name"; value=thisController.newNameL2; break;
			case "DL1": fieldType="description"; value=thisController.newDescL1; break;
			case "DL2": fieldType="description"; value=thisController.newDescL2; break;
			default: console.log("no field given!");
		}
		var promise = avProcessTypeService.updateProcessTypeData(processType.id,fieldType,value);
		promise.then(function(data) {
				reload();
			 },
			 function(data) {
				console.log('error');
				console.log(data);
			 }
			);
	}
	if (keyCode===27) {		// Escape key pressed
		thisController.editNL1 = false;
		thisController.editNL2 = false;
		thisController.editDL1 = false;
		thisController.editDL2 = false;
		thisController.newNameL1 = processType.nameLang(languages[0].key);
		thisController.newNameL2 = processType.nameLang(languages[1].key);
		thisController.newDescL1 = processType.descLang(languages[0].key);
		thisController.newDescL2 = processType.descLang(languages[1].key);
	}
  }

  
  
  this.down=function(index){
	  var id1=thisController.parametergrps[index].id;
	  var id2=thisController.parametergrps[index+1].id;
	  var pos1=thisController.parametergrps[index+1].pos;
	  var pos2=thisController.parametergrps[index].pos;
	  var promise = avProcessTypeService.exPosPTParamGrp(id1,pos1,id2,pos2);
	  promise.then(function(){reload()},function(){console.log("error")})
  }

  
  this.up=function(index){
	  var id1=thisController.parametergrps[index-1].id;
	  var id2=thisController.parametergrps[index].id;
	  var pos1=thisController.parametergrps[index].pos;
	  var pos2=thisController.parametergrps[index-1].pos;
	  var promise = avProcessTypeService.exPosPTParamGrp(id1,pos1,id2,pos2);
	  promise.then(function(){reload()},function(){console.log("error")})
  }
  
  
  
  this.keyUpPG = function(keyCode) {
	if (keyCode===13) {				// Return key pressed
		console.log("Hallo");
		this.addParameterGroup();
	}
	if (keyCode===27) {		// Escape key pressed
		this.editmode=false;
	}
  }
  
  
  
  this.newParameterGroup=function(){
	  thisController.editmode=true;
  }
  
  
  
 this.addParameterGroup=function(){
	 // add a new ParameterGroup to the database.
	var name={}
	name[languages[0].key]=thisController.newGrpNameL1;
	name[languages[1].key]=thisController.newGrpNameL2;
	var position=0;
	if (thisController.parametergrps){
		position=thisController.parametergrps.length+1;
	}
	var promise = avProcessTypeService.addPTParameterGrp(processType.id,position,name);
	promise.then(function(data) {
			reload();
		 },
		 function(data) {
			console.log('error');
			console.log(data);
		 }
		);
 }
 

};

angular.module('unidaplan').controller('editPtParamGrpsController', ['$state','$stateParams','$translate','$scope',
       'restfactory','processService','processType','languages','avProcessTypeService',editPtParamGrpsController]);

})();