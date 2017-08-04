(function(){
  'use strict';

function editParamController($scope,$state,$stateParams,
		$translate,parameterService,restfactory,languages,parameters,sampleTypes){

//  for (var i = 0; i < parameters.length; i++){  
//	  if (parameters[i].id == $stateParams.parameterID){
//		  index = i;
//	  }

  
  var param = parameters.filter(function(x){return x.id == $stateParams.parameterID})[0];
  
  if (param == undefined){
	  console.log("ERROR! Parameter doesn't exist");
  }
  

  
//  $scope.sortableOptions = {
//	        containment: '#table-container',
//	        containerPositioning: 'relative'
//	    };
    
  var thisController = this;
  
  this.sampleTypes = sampleTypes;
  
  if (param.sampletype != undefined){
	  this.sampleType = this.sampleTypes.filter(function(x){return x.id==param.sampletype})[0];
  }
      
  this.lang1 = $translate.instant(languages[0].name);
  
  this.lang2 = $translate.instant(languages[1].name);
  
  this.lang1Key = languages[0].key;
  
  this.lang2Key = languages[1].key;

  this.dataTypes = ["undefined","integer","float","measurement","string",
                    "longstring","chooser","date","checkbox","timestamp",
                    "url","email","sample"];
  
  this.originalDataType = param.datatype;
  
  this.dataType = param.datatype;
      
  this.descL1 = param.descLang(this.lang1Key);
  
  this.descL2 = param.descLang(this.lang2Key);
  
  this.editNL1 = $stateParams.newParameter === 'true';
    
  this.newDescL1 = param.descLang(this.lang1Key);
  
  this.newDescL2 = param.descLang(this.lang2Key);
  
  if (param.stringkeyunit){
	  this.unitL1 = param.unitLang(this.lang1Key);
	  this.newUnitL1 = param.unitLang(this.lang1Key);
	  this.unitL2 = param.unitLang(this.lang2Key);
	  this.newUnitL2 = param.unitLang(this.lang2Key);
  } else {
	  this.unitL1 = "";
	  this.newUnitL1 = "";
	  this.unitL2 = "";
	  this.newUnitL2 = "";
  }	
  
  $scope.possibleValues = param.possiblevalues;
  
  if ($stateParams.newPossvalue == 'true') {
	  $scope.possibleValues[$scope.possibleValues.length-1].edit = true;
  }
  
  this.format = param.format;
  
  this.newFormat = param.format;
  
  this.languages = languages;
  
  this.min = param.min;

  this.newMin = param.min;
  
  this.max = param.max;

  this.newMax = param.max;
  
  this.newDataType = param.datatype;
    
  this.nameL1 = param.nameLang(this.lang1Key);
  
  if (param.nameLang(this.lang1Key) != '-'){
	  this.newNameL1 = param.nameLang(this.lang1Key);
  }
  
  this.nameL2 = param.nameLang(this.lang2Key);

  if (param.nameLang(this.lang2Key) != '-'){
	  this.newNameL2 = param.nameLang(this.lang2Key);
  }
  
  if (param.descLang(this.lang1Key) != '-'){
	  this.newDescriptionL1 = param.descLang(this.lang1Key);
  }
  
  if (param.descLang(this.lang2Key) != '-'){
	  this.newDescriptionL2 = param.descLang(this.lang2Key);
  }
	  
  this.parameters = parameters;
  
  this.regex = param.regex;
  
  this.newRegex = param.regex;

  
  
  this.addPossibleValue = function(){
	  var promise = parameterService.addPossibleValue("new Value",$stateParams.parameterID);
	  promise.then(function(){reload(true);});
  };
  
  
  
  this.cancelEdit = function(){
	  thisController.newUnitL1 = thisController.unitL1;
	  thisController.newUnitL2 = thisController.unitL2;
	  thisController.newNameL1 = thisController.nameL1;
	  thisController.newNameL2 = thisController.nameL2;
	  thisController.newDescL1 = thisController.descL1;
	  thisController.newDescL2 = thisController.descL2;
	  thisController.newRegex = thisController.regex;
	  thisController.newMin = thisController.min;
	  thisController.newMax = thisController.max;
	  thisController.editNL1 = false;
	  thisController.editNL2 = false;
	  thisController.editDL1 = false;
	  thisController.editDL2 = false;
	  thisController.editUL1 = false;
	  thisController.editUL2 = false;
	  thisController.editFormat = false;
	  thisController.editMin = false;
	  thisController.editMax = false;
	  thisController.editRegex = false;
  }
  
  
  
  this.dataTypeChanged = function(){
	  return thisController.dataType != thisController.originalDataType;
  };

  
    
  this.deleteParameter = function(parameter){
	  var promise = parameterService.deleteParameter(param.id);
	  promise.then(function(){reload();},function(){console.log("error");});
  };
  
  
  
  this.deletePossibleValue = function(pvalue){
	  var promise = parameterService.deletePossibleValue(pvalue.id);
	  promise.then(function(){reload();},function(){console.log("error");});
  };
  
  
  
  this.edit = function(field){
	  thisController.editNL1 	= (field == "NL1");
	  thisController.editNL2	= (field == "NL2");
	  thisController.editDL1 	= (field == "DL1");
	  thisController.editDL2 	= (field == "DL2");
	  thisController.editUL1 	= (field == "UL1");
	  thisController.editUL2 	= (field == "UL2");
	  thisController.editRegex 	= (field == "regex");
	  thisController.editFormat = (field == "format");
	  thisController.editMin 	= (field == "min");
	  thisController.editMax 	= (field == "max");
  };
	
  
  this.hasUnit = function() {
	  var a = ['integer','measurement','float'].indexOf(thisController.dataType);
	  return a > -1;
  }
  
  
  
  this.keyUp = function(keyCode,field,newValue) {
	  if (keyCode === 13) {				// Return key pressed
		  var newParam = {parameterid:$stateParams.parameterID};
	  	  newParam[field] = newValue; 
		  var promise = parameterService.updateParameter(newParam);
		  promise.then(function(){reload()});
	  }
	  if (keyCode === 27) {		// Escape key pressed
		  thisController.cancelEdit();
	  }
  };
  
  
  
  this.keyUpDescription = function(keyCode,language,newValue) {
	  if (keyCode === 13) {				// Return key pressed
		  var newParam = {parameterid:$stateParams.parameterID, description : {} };
	  	  newParam.description[language] = newValue;
		  var promise = parameterService.updateParameter(newParam);
		  promise.then(function(){reload()});
	  }
	  if (keyCode === 27) {		// Escape key pressed
		  thisController.cancelEdit();
	  }
  };
  
  
  
  this.keyUpName = function(keyCode,language,newValue) {
	  if (keyCode === 13) {				// Return key pressed
		  var newParam = {parameterid:$stateParams.parameterID, name :{} };
	  	  newParam.name[language] = newValue;
		  var promise = parameterService.updateParameter(newParam);
		  promise.then(function(){reload()});
	  }
	  if (keyCode === 27) {		// Escape key pressed
		  thisController.cancelEdit();
	  }
  };
  
  
  
  this.keyUpUnit = function(keyCode,language,newValue) {
	  if (keyCode === 13) {				// Return key pressed
		  var newParam = {parameterid:$stateParams.parameterID, unit : {} };
	  	  newParam.unit[language] = newValue;
		  var promise = parameterService.updateParameter(newParam);
		  promise.then(function(){reload()});
	  }
	  if (keyCode === 27) {		// Escape key pressed
		  thisController.cancelEdit();
	  }
  };
  
  
  this.keyUpPValue = function (item,keyCode){
	  if (keyCode === 13) {				// Return key pressed
		  item.edit = false
		  var promise = parameterService.updatePossibleValue(item.id,item.string);
		  promise.then (function(){reload();})
	  }
  }
  
  
  
  $scope.dragControlListeners = {
		accept: function (sourceItemHandleScope, destSortableScope) {return true},
			//override to determine drag is allowed or not. default is true.
		itemMoved: function (event) {console.log ("order changed")
			  	var neworder=[];
		  	for (var i=0; i<$scope.possibleValues.length; i++){
		  		neworder.push($scope.possibleValues[i].id);
		  	}
			var promise=parameterService.reorderPossibleValues($stateParams.parameterID,neworder);
			//		promise.then(function(){reload();})
		},
	    orderChanged: function(event) {
	      	var neworder = [];
		  	for (var i = 0; i < $scope.possibleValues.length; i++)
		  		neworder.push($scope.possibleValues[i].id);
			var promise=parameterService.reorderPossibleValues($stateParams.parameterID,neworder);
			//		promise.then(function(){reload();})
	    },
	    allowDuplicates: false //optional param allows duplicates to be dropped.
  };

//	$scope.dragControlListeners1 = {
//	        containment: '#board'//optional param.
//	        allowDuplicates: true //optional param allows duplicates to be dropped.
//	};
		
  
//  $scope.$watchCollection('possibleValues', 
  
  
  this.showFormat = function(){
	  if (['integer','float','date','timestamp'].indexOf(thisController.dataType)>-1){
		  return true;
	  }else{ 
		  return false;
	  }
  }
  
  
  
  this.showMinMaxNumber = function(){
	  if (['float','measurement','integer'].indexOf(thisController.dataType) > -1){
		  return true;
	  }else{ 
		  return false;
	  }
  }
  

  
  
  this.showMinMaxTimestamp = function(){
	  if (["date","timestamp"].indexOf(thisController.dataType)>-1){
		  return true;
	  }else{ 
		  return false;
	  }
  }
  
  
  
 
  
  var reload = function(newPossvalue) {
  	var current = $state.current;
  	var params = angular.copy($stateParams);
  	params.newPossvalue=newPossvalue;
  	params.newParameter=false;
  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  };
  
  
  
  this.update = function(field,newValue){
	var parameter = { parameterid : $stateParams.parameterID };
	parameter[field] = newValue;
	parameterService.updateParameter(parameter);
  }
	
  
};

angular.module('unidaplan').controller('editParamController', ['$scope','$state','$stateParams','$translate',
             'parameterService','restfactory','languages','parameters','sampleTypes',editParamController]);

})();