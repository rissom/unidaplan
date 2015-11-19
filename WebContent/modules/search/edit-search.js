(function(){
'use strict';

function editSearchController(restfactory,$state,$translate,key2string,sampleTypes,ptypes,languages){
	
	var thisController = this;
	
	this.sampleTypes=sampleTypes;
	
	this.processTypes=ptypes;
				
	this.searchTypes=[$translate.instant('Sample'),$translate.instant('Process'),$translate.instant('object specific processparameters')];
	
	this.groups = [$translate.instant('public'),$translate.instant('only me')]
	
	this.operators = ["⋀","⋁"];
	
	this.comparators = ["<",">","=","not"];
	
	this.sampleParameters = [{name:"halli"}, {name:"hallo"}, {name:"hallo2"}]
	
	this.languages=languages;
	  
	this.nameL1 = "Horst" //parameterGrp.nameLang(languages[0].key);
	  
	this.newNameL1 = "Peter" //parameterGrp.nameLang(languages[0].key);
	 
	this.nameL2 = "John" //parameterGrp.nameLang(languages[1].key);

	this.newNameL2 = "John" //parameterGrp.nameLang(languages[1].key);
	    
	this.lang1=$translate.instant(languages[0].name);
	  
	this.lang2=$translate.instant(languages[1].name);
	  
	this.lang1key=$translate.instant(languages[0].key);
	  
	this.lang2key=$translate.instant(languages[1].key);
	 
	this.editFieldNL1=false;
	  
	this.editFieldNL2=false;
	
	this.searchType=1;
	
	this.changeSampleType = function () {
		console.log ("changing sampletype");
		// Parameter laden
	}
	
//	this.groups += alle meine Projektgruppen
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
		}
	}
		
	this.addSearch = function() {
		// searchService.saveSearch
	}
	
	this.getSampleType = function(id) {sampleTypeID
		return sampleService.loadSample(sampleID)
	}
   
}  


angular.module('unidaplan').controller('editSearchController',['restfactory','$state','$translate','key2string','sampleTypes','ptypes','languages',editSearchController]);

})();