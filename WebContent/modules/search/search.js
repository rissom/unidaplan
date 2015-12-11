(function(){
'use strict';

function searchController(restfactory,$state,$stateParams,$translate,
		key2string,search,languages,searchService){
		
	var thisController = this;
															
	this.languages = languages;
	
	this.parameters = search.parameter;
	
	this.comparators = [{index:1,label:"<"},{index:2,label:">"},{index:3,label:"="},{index:4,label:"not"}];
	
	this.search=search;
	  
	this.nameL1 = search.nameL1; //parameterGrp.nameLang(languages[0].key);
	  
	this.newNameL1 = search.nameL1; //parameterGrp.nameLang(languages[0].key);
	 
	this.nameL2 = search.nameL2; //parameterGrp.nameLang(languages[1].key);

	this.newNameL2 = search.nameL2; //parameterGrp.nameLang(languages[1].key);
		    
	this.lang1 = $translate.instant(languages[0].name);
	  
	this.lang2 = $translate.instant(languages[1].name);
	  
	this.lang1key = $translate.instant(languages[0].key);
	  
	this.lang2key = $translate.instant(languages[1].key);
	 	  
	this.editFieldNL2 = false;
	
	this.searchType=1;
	
	var types=[$translate.instant('all samples'),$translate.instant('all processes'),$translate.instant('all samples in a specific process')];
	
	var modeAnd=$translate.instant('all of the following');
	
	var modeOr=$translate.instant('one of the');

	
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			thisController.startSearch();
		}
	};
	
	
	
	this.keyUp = function(keyCode,name,language) {
		if (keyCode===13) {				// Return key pressed
			var promise=searchService.updateSearchName(search.id,name, language);	
			promise.then(function(){
				reload();
			},function(){
				console.log("error");
			});
		}
		if (keyCode===27) {		// Escape key pressed
			  thisController.editmode=false;
		}
	};

	
	
	this.getSampleType = function(id) {
		return sampleService.loadSample(sampleID)
	};
  
    
    
    var reload=function() {
    	var current = $state.current;
    	var params = angular.copy($stateParams);
    	params.newSearch=false;
    	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };
    
    
	this.startSearch = function() {
		var searchParams = {
				searchid:thisController.search.id,
				parameters:thisController.parameters
		};
		console.log ("search JSON:",searchParams);
		$state.go("result",{searchParams:searchParams});
//		return searchService.startSearch({searchid:thisController.search.id,parameters:thisController.parameters});
	};
	
	
	
	this.type = function() {
		return types[search.type-1];
	}
	
	
	
	this.mode = function() {
		return search.operation ? modeAnd : modeOr;
	}
}  


angular.module('unidaplan').controller('searchController',['restfactory','$state','$stateParams','$translate',
                                                   		'key2string','search','languages','searchService',searchController]);

})();