(function(){
  'use strict';

function editParamController($scope,$state,$stateParams,$translate,parameterService,restfactory,parameters,languages){

  var index=-1;
  for (var i=0; i<parameters.length; i++){  
	  if (parameters[i].id==$stateParams.parameterID){
		  index=i;
	  }
  }
  if (index==-1){
	  console.log("ERROR! Parameter doesn't exist");
  }
  
  $scope.sortableOptions = {
	        containment: '#table-container',
	        containerPositioning: 'relative'
	    };
  
  
  $scope.foo = ['foo 1', '..'];
  $scope.bar = ['bar 1', '..'];
  $scope.barConfig = {
      group: 'foobar',
      animation: 150,
      onSort: function (/** ngSortEvent */evt){
          // @see https://github.com/RubaXa/Sortable/blob/master/ng-sortable.js#L18-L24
      }
  };
  
//  $scope.$watchCollection('possibleValues', function (newOrder,oldOrder){ $scope.possibleValues.map(function(pv){ console.log(pv.string)});});
  
  var thisController=this;
  
  this.lang1=$translate.instant(languages[0].name);
  
  this.lang2=$translate.instant(languages[1].name);
  
  this.lang1Key=languages[0].key;
  
  this.lang2Key=languages[1].key;

  this.dataTypes=["undefined","integer","float","measurement","string","longstring","chooser","date","checkbox","timestamp","url","email"];
  
  this.originalDataType=parameters[index].datatype;
  
  this.dataType=parameters[index].datatype;
    
  this.descL1=parameters[index].descLang(this.lang1Key);
  
  this.descL2=parameters[index].descLang(this.lang2Key);
  
  this.newDescL1=parameters[index].descLang(this.lang1Key);
  
  this.newDescL2=parameters[index].descLang(this.lang2Key);
  
  $scope.possibleValues=parameters[index].possiblevalues;
  
  this.format=parameters[index].format;
  
  this.newFormat=parameters[index].format;
  
  this.languages=languages;
  
  this.min=parameters[index].min;

  this.newMin=parameters[index].min;
  
  this.max=parameters[index].max;

  this.newMax=parameters[index].max;
  
  this.newDataType=parameters[index].datatype;
    
  this.nameL1=parameters[index].nameLang(this.lang1Key);
  
  if (parameters[index].nameLang(this.lang1Key)!='-'){
	  this.newNameL1=parameters[index].nameLang(this.lang1Key);
  }
  
  this.nameL2=parameters[index].nameLang(this.lang2Key);

  if (parameters[index].nameLang(this.lang2Key)!='-'){
	  this.newNameL2=parameters[index].nameLang(this.lang2Key);
  }
  
  if (parameters[index].descLang(this.lang1Key)!='-'){
	  this.newDescriptionL1=parameters[index].descLang(this.lang1Key);
  }
  
  if (parameters[index].descLang(this.lang2Key)!='-'){
	  this.newDescriptionL2=parameters[index].descLang(this.lang2Key);
  }
	  
  this.parameters=parameters;
  
  this.regex=parameters[index].regex;
  
  this.newRegex=parameters[index].regex;

  
  this.addPossibleValue = function(){
	  var promise = parameterService.addPossibleValue("new Value",$stateParams.parameterID);
	  promise.then(function(){reload();});
  };
  
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
	  thisController.editRegex 	= (field=="regex");
	  thisController.editFormat = (field=="format");
	  thisController.editMin 	= (field=="min");
	  thisController.editMax 	= (field=="max");
  };
	
  
  this.keyUp = function(keyCode) {
		 console.log ("halli");
	if (keyCode===13) {				// Return key pressed
		thisController.update();
	}
	if (keyCode===27) {		// Escape key pressed
		parameter.editing=false;
	}
  };
  
  
  
  this.keyUpPValue = function (item,keyCode){
	  if (keyCode===13) {				// Return key pressed
		  item.edit=false
		  var promise = parameterService.updatePossibleValue(item.id,item.string);
		  promise.then (function(){reload();})
	  }
  }
  
  
  
  this.showFormat = function(){
	  if (['string','long string','measurement','chooser','checkbox','url','email'].indexOf(thisController.dataType)>-1){
		  return false;
	  }else{ 
		  return true;
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
		  var newParam={
			name:name,
			description:description,
			regex: thisController.regex,
			format:thisController.format,
			min:thisController.min,
			max:thisController.max,
			datatype:thisController.dataType,
			parameterid:$stateParams.parameterID
		  }
		  var promise = parameterService.updateParameter(newParam);
		  promise.then(function(){reload()});
  }
  
  var reload=function() {
  	var current = $state.current;
  	var params = angular.copy($stateParams);
  	params.newSearch=false;
  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  };
  
};

angular.module('unidaplan').controller('editParamController', ['$scope','$state','$stateParams','$translate',
             'parameterService','restfactory','parameters','languages',editParamController]);

})();