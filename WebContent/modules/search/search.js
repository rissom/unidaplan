(function(){
'use strict';

function searchController(restfactory,$state,$stateParams,$translate,
		key2string,search,languages,searchService){
		
	var thisController = this;
															
	this.languages = languages;
	
	this.searchid = $stateParams.id;
	
	this.parameters = search.parameters;
	
	this.editable = search.editable;
	
	this.sparameters = search.sparameters;
	
	this.pparameters = search.pparameters;
	
	var allComparators = [{index:1,label:"<"},{index:2,label:">"},{index:3,label:"="},{index:4,label:"not"},{index:5,label:$translate.instant("contains")}];
	
	this.comparators={ 	
			integer		: allComparators.slice(0,4), 
			float 		: allComparators.slice(0,4),
 			measurement : allComparators.slice(0,4),
 			string		: allComparators.slice(2),
 			longstring	: allComparators.slice(2),
 			chooser		: allComparators.slice(2),
 			date		: allComparators.slice(0,4),
 			checkbox	: allComparators.slice(2),
 			timestamp	: allComparators.slice(0,4),
 			URL			: allComparators.slice(2),
 			email		: allComparators.slice(2)
		};
	
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
		
	var types=[
	           $translate.instant('all objects'),
	           $translate.instant('all processes'),
	           $translate.instant('all objects in a specific process'),
	           $translate.instant('all objects with processes')];
	
	var modeAnd=$translate.instant('all of the following');
	
	var modeOr=$translate.instant('one of the');


	this.mode = function() {
		return search.operation ? modeAnd : modeOr;
	}
	
	
	this.keyUp = function(keyCode) {
		if (keyCode===13) {				// Return key pressed
			thisController.startSearch();
		}
	};
	
	
	
//	this.keyUp = function(keyCode,name,language) {
//		if (keyCode===13) {				// Return key pressed
//			var promise=searchService.updateSearchName(search.id,name, language);	
//			promise.then(function(){
//				reload();
//			},function(){
//				console.log("error");
//			});
//		}
//		if (keyCode===27) {		// Escape key pressed
//			  thisController.editmode=false;
//		}
//	};
	
	
//	this.getSampleType = function(id) {
//		return sampleService.loadSample(sampleID)
//	};
  
    
    
    var reload=function() {
    	var current = $state.current;
    	var params = angular.copy($stateParams);
    	params.newSearch=false;
    	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };
    
    
    
	this.startSearch = function() {
		var searchParams = { searchid:thisController.search.id, 
							 searchtype: search.type 
						   };
		
		switch (search.type){
		
			case 1:
				searchParams.parameters=thisController.sparameters;
				$state.go("result",{searchParams:searchParams});
				break;
				
			case 2:
				searchParams.parameters=thisController.pparameters;
				$state.go("result",{searchParams:searchParams});
				break;
				
			case 3:
				searchParams.parameters=thisController.poparameters;
				$state.go("result",{searchParams:searchParams});
				break;
			
			case 4:
				searchParams.pparameters=thisController.pparameters;
				searchParams.sparameters=thisController.sparameters;
				$state.go("result",{searchParams:searchParams});
				break;
				
			default: console.log("Error! Unknown search type");
		}
		
	};
	
	
	
	this.type = function() {
		return types[search.type-1];
	}
	
	
}  


angular.module('unidaplan').controller('searchController',['restfactory','$state','$stateParams','$translate',
                                                   		'key2string','search','languages','searchService',searchController]);

})();