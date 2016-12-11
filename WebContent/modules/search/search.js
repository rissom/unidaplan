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
	
	this.poparameters = search.poparameters;

	
	
	var allComparators = [{index:0,label:"<"},
	                      {index:1,label:"<="},
	                      {index:2,label:"="},
	                      {index:3,label:">="},
	                      {index:4,label:">"},
	                      {index:5,label:"!="},
	                      {index:6,label:$translate.instant("contains")}];
	

	
	this.comparators={ 	integer		:  allComparators.slice(0,6), 
 						float 		:  allComparators.slice(0,6),
			 			measurement :  allComparators.slice(0,6),
			 			string		: [allComparators[6],allComparators[2],allComparators[5]],
			 			longstring	:  allComparators.slice(4),
			 			chooser		:  allComparators.slice(4),
			 			date		:  allComparators.slice(0,6),
			 			checkbox	: [allComparators[2],allComparators[5]],
			 			timestamp	:  allComparators.slice(0,6),
			 			URL			: [allComparators[6],allComparators[2],allComparators[5]],
			 			email		: [allComparators[6],allComparators[2],allComparators[5]]
					};
	
	this.search=search;
	  
	this.nameL1 = search.nameL1; //parameterGrp.nameLang(languages[0].key);
	  
	this.newNameL1 = search.nameL1; //parameterGrp.nameLang(languages[0].key);
	 
	this.nameL2 = search.nameL2; //parameterGrp.nameLang(languages[1].key);

	this.newNameL2 = search.nameL2; //parameterGrp.nameLang(languages[1].key);
		    
	this.lang1 = $translate.instant(languages[0].name);
	  
	this.lang2 = $translate.instant(languages[1].name);
	  
	this.lang1key = languages[0].key;
	  
	this.lang2key = languages[1].key;
	 	  
	this.editFieldNL2 = false;
		
	var types = ['all samples',
	             'all processes',
	             'all samples in a specific process',
	             'all samples with processes'];
	
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
		return $translate.instant(types[search.type-1]);
	}
	
	
}  


angular.module('unidaplan').controller('searchController',['restfactory','$state','$stateParams','$translate',
                                                   		'key2string','search','languages','searchService',searchController]);

})();