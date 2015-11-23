(function(){
  'use strict';

function aSamplesController($state,$stateParams,$translate,restfactory,sampleService,types,languages){
  
  var thisController=this;
  
  this.languages=languages;
  
  this.lang1=$translate.instant(languages[0].name);
  
  this.lang2=$translate.instant(languages[1].name);
  
  this.types=types;
    
  
  this.newSampleType=function(){
	  thisController.editmode=true;
  }
  
 
  
  this.performAction = function(index,sampleType){
	  	if (index==0){
			$state.go("editSTParamGrps",{sampleTypeID:sampleType.id});
	  	}
	  	if (index==1){
	  		var promise=sampleService.deleteSampleType(sampleType.id);
	  		promise.then(function(){reload();},function(){console.log("error");})
	  	}

  }
  
  
  
  this.keyUp = function(keyCode) {
	if (keyCode===13) {				// Return key pressed
		this.addSampleType();
	}
	if (keyCode===27) {		// Escape key pressed
		thisController.editmode=false;
	}
  }
  
  
  
  
  this.addSampleType=function(){
	  var name={};
	  name[languages[0].key]=this.newNameL1;
	  name[languages[1].key]=this.newNameL2;
	  var description={};
	  description[languages[0].key]=this.newDescL1;
	  description[languages[1].key]=this.newDescL2; 	  
	  var newSampleType={"name":name,"description":description};	  
	  var promise = sampleService.addSampleType(newSampleType);
	  promise.then(function(){ reload();},function(){console.log("error");})
  }
  
 

};

angular.module('unidaplan').controller('aSamplesController', ['$state','$stateParams','$translate','restfactory','sampleService','types','languages',aSamplesController]);

})();