(function(){
  'use strict';

function editParamController($scope,$state,$stateParams,$translate,parameterService,restfactory,parameters,languages){

  var index = -1;
  for (var i = 0; i < parameters.length; i++){  
	  if (parameters[i].id == $stateParams.parameterID){
		  index = i;
	  }
  }
  if (index == -1){
	  console.log("ERROR! Parameter doesn't exist");
  }
  

  
//  $scope.sortableOptions = {
//	        containment: '#table-container',
//	        containerPositioning: 'relative'
//	    };
    
  var thisController = this;
      
  this.lang1 = $translate.instant(languages[0].name);
  
  this.lang2 = $translate.instant(languages[1].name);
  
  this.lang1Key = languages[0].key;
  
  this.lang2Key = languages[1].key;

  this.dataTypes = ["undefined","integer","float","measurement","string","longstring","chooser","date","checkbox","timestamp","url","email"];
  
  this.originalDataType = parameters[index].datatype;
  
  this.dataType = parameters[index].datatype;
      
  this.descL1 = parameters[index].descLang(this.lang1Key);
  
  this.descL2 = parameters[index].descLang(this.lang2Key);
  
  this.editNL1 = $stateParams.newParameter === 'true';
    
  this.newDescL1 = parameters[index].descLang(this.lang1Key);
  
  this.newDescL2 = parameters[index].descLang(this.lang2Key);
  
  if (parameters[index].stringkeyunit){
	  this.unitL1=parameters[index].unitLang(this.lang1Key);
	  this.newUnitL1=parameters[index].unitLang(this.lang1Key);
	  this.unitL2=parameters[index].unitLang(this.lang2Key);
	  this.newUnitL2=parameters[index].unitLang(this.lang2Key);
  } else {
	  this.unitL1="";
	  this.newUnitL1="";
	  this.unitL2="";
	  this.newUnitL2="";
  }	
  
  $scope.possibleValues = parameters[index].possiblevalues;
  
  if ($stateParams.newPossvalue == 'true') {
	  $scope.possibleValues[$scope.possibleValues.length-1].edit = true;
  }
  
  this.format = parameters[index].format;
  
  this.newFormat = parameters[index].format;
  
  this.languages = languages;
  
  this.min = parameters[index].min;

  this.newMin = parameters[index].min;
  
  this.max = parameters[index].max;

  this.newMax = parameters[index].max;
  
  this.newDataType = parameters[index].datatype;
    
  this.nameL1 = parameters[index].nameLang(this.lang1Key);
  
  if (parameters[index].nameLang(this.lang1Key) != '-'){
	  this.newNameL1=parameters[index].nameLang(this.lang1Key);
  }
  
  this.nameL2=parameters[index].nameLang(this.lang2Key);

  if (parameters[index].nameLang(this.lang2Key) != '-'){
	  this.newNameL2=parameters[index].nameLang(this.lang2Key);
  }
  
  if (parameters[index].descLang(this.lang1Key) != '-'){
	  this.newDescriptionL1=parameters[index].descLang(this.lang1Key);
  }
  
  if (parameters[index].descLang(this.lang2Key) != '-'){
	  this.newDescriptionL2=parameters[index].descLang(this.lang2Key);
  }
	  
  this.parameters = parameters;
  
  this.regex = parameters[index].regex;
  
  this.newRegex = parameters[index].regex;

  
  this.addPossibleValue = function(){
	  var promise = parameterService.addPossibleValue("new Value",$stateParams.parameterID);
	  promise.then(function(){reload(true);});
  };
  
  this.cancelEdit = function(){
	  thisController.newUnitL1=thisController.unitL1;
	  thisController.newUnitL2=thisController.unitL2;
	  thisController.newNameL1=thisController.nameL1;
	  thisController.newNameL2=thisController.nameL2;
	  thisController.newDescL1=thisController.descL1;
	  thisController.newDescL2=thisController.descL2;
	  thisController.newRegex=thisController.regex;
	  thisController.newMin=thisController.min;
	  thisController.newMax=thisController.max;
	  thisController.editNL1=false;
	  thisController.editNL2=false;
	  thisController.editDL1=false;
	  thisController.editDL2=false;
	  thisController.editUL1=false;
	  thisController.editUL2=false;
	  thisController.editFormat=false;
	  thisController.editMin=false;
	  thisController.editMax=false;
	  thisController.editRegex=false;
  }
  
  this.dataTypeChanged=function(){
	  return thisController.dataType!=thisController.originalDataType;
  };

    
  this.deleteParameter = function(parameter){
	  var promise = parameterService.deleteParameter(index);
	  promise.then(function(){reload();},function(){console.log("error");});
  };
  
  
  
  this.deletePossibleValue = function(pvalue){
	  var promise=parameterService.deletePossibleValue(pvalue.id);
	  promise.then(function(){reload();},function(){console.log("error");});
  };
  
  
  
  this.edit = function(field){
	  thisController.editNL1 	= (field=="NL1");
	  thisController.editNL2	= (field=="NL2");
	  thisController.editDL1 	= (field=="DL1");
	  thisController.editDL2 	= (field=="DL2");
	  thisController.editUL1 	= (field=="UL1");
	  thisController.editUL2 	= (field=="UL2");
	  thisController.editRegex 	= (field=="regex");
	  thisController.editFormat = (field=="format");
	  thisController.editMin 	= (field=="min");
	  thisController.editMax 	= (field=="max");
  };
	
  
  this.hasUnit = function() {
	  var a=['integer','measurement','float'].indexOf(thisController.dataType);
	  return a>-1;
  }
  
  
  this.keyUp = function(keyCode) {
	if (keyCode===13) {				// Return key pressed
		thisController.update();
	}
	if (keyCode===27) {		// Escape key pressed
		thisController.cancelEdit();
	}
  };
  
  
  
  this.keyUpPValue = function (item,keyCode){
	  if (keyCode === 13) {				// Return key pressed
		  item.edit=false
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
	  if (['string','long string','measurement','chooser','checkbox','url','email'].indexOf(thisController.dataType)>-1){
		  return false;
	  }else{ 
		  return true;
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
  
  
  
  this.update = function(){
		  thisController.regex=thisController.newRegex;
		  thisController.format=thisController.newFormat;
		  thisController.min=thisController.newMin;
		  thisController.max=thisController.newMax;
		  var name={};
		  name[thisController.lang1Key]=thisController.newNameL1;
		  name[thisController.lang2Key]=thisController.newNameL2;
		  var description={};
		  description[thisController.lang1Key]=thisController.newDescL1;
		  description[thisController.lang2Key]=thisController.newDescL2;

		  var newParam = {
			name:name,
			description:description,
			regex: thisController.regex,
			format:thisController.format,
			min:thisController.min,
			max:thisController.max,
			datatype:thisController.dataType,
			parameterid:$stateParams.parameterID
		  }
		  
		  var unit = {};
		  if (thisController.newUnitL1 && thisController.newUnitL1!=""){
			  unit[thisController.lang1Key]=thisController.newUnitL1;
		  }
		  if (thisController.newUnitL2 && thisController.newUnitL2!=""){
			  unit[thisController.lang2Key]=thisController.newUnitL2;
		  }
		  		  
		  if (thisController.hasUnit() && Object.keys(unit).length>0){
			  newParam.unit=unit;
		  }
		  var promise = parameterService.updateParameter(newParam);
		  promise.then(function(){reload()});
  }
  
  var reload=function(newPossvalue) {
  	var current = $state.current;
  	var params = angular.copy($stateParams);
  	params.newPossvalue=newPossvalue;
  	params.newParameter=false;
  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  };
  
};

angular.module('unidaplan').controller('editParamController', ['$scope','$state','$stateParams','$translate',
             'parameterService','restfactory','parameters','languages',editParamController]);

})();