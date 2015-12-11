(function(){
'use strict';

function resultController(restfactory,result,$state,$translate,avSampleTypeService,types,
		key2string,languages){
		
	var thisController = this;
	
	this.result = result;
															
//	this.languages = languages;
//	
//	this.parameters = search.parameter;
//	
//	this.comparators = [{index:1,label:"<"},{index:2,label:">"},{index:3,label:"="},{index:4,label:"not"}];
//	
//	this.search=search;
//	  
//	this.nameL1 = search.nameL1; //parameterGrp.nameLang(languages[0].key);
//	  
//	this.newNameL1 = search.nameL1; //parameterGrp.nameLang(languages[0].key);
//	 
//	this.nameL2 = search.nameL2; //parameterGrp.nameLang(languages[1].key);
//
//	this.newNameL2 = search.nameL2; //parameterGrp.nameLang(languages[1].key);
//		    
//	this.lang1 = $translate.instant(languages[0].name);
//	  
//	this.lang2 = $translate.instant(languages[1].name);
//	  
//	this.lang1key = $translate.instant(languages[0].key);
//	  
//	this.lang2key = $translate.instant(languages[1].key);
//	 	  
//	this.editFieldNL2 = false;
//	
//	this.searchType=1;
//	
//	
//	
//	this.keyUp = function(keyCode,newValue,parameter) {
//		if (keyCode===13) {				// Return key pressed
//		}
//	};
//	
//	
//	
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
//
//	
	
	this.getType = function(type){
		return avSampleTypeService.getType({typeid:type},types);
	};

  
    
	
    
    var reload=function() {
    	var current = $state.current;
    	var params = angular.copy($stateParams);
    	params.newSearch=false;
    	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };
}  


angular.module('unidaplan').controller('resultController',['restfactory','result','$state','$translate',
                             'avSampleTypeService','types','key2string','languages',resultController]);

})();