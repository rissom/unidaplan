(function(){
  'use strict';

function aSamplesController($state,$stateParams,$translate,restfactory,avSampleTypeService,sampleService,types,languages){
  
	var thisController = this;
  
	this.languages = languages;
  
	this.lang1 = $translate.instant(languages[0].name);
  
	this.lang2 = $translate.instant(languages[1].name);
  
	this.stypes = [];
	
	// put sampletypes in the correct format for string-parameter directive
	for (let type of types){
	    this.stypes.push({nl1:{data: {value:type.nameLang(languages[0].key)}, 
	                           field:"name",
	                           lang: languages[0].key,
	                           id: type.id},
	                      nl2:{data: {value:type.nameLang(languages[1].key)}, 
	                           field:"name",
	                           lang: languages[1].key,
	                           id: type.id},
	                      dl1:{data: {value:type.descLang(languages[0].key)}, 
	                           field:"description",
	                           lang: languages[0].key,
	                           id: type.id},
	                      dl2:{data: {value:type.descLang(languages[1].key)},
	                           field:"description",
	                           lang: languages[1].key,
	                           id: type.id},
	                      actions: type.actions,
	                      id: type.id,
	                      deletable : type.deletable})
	}
	
	

  	this.newSampleType = function(){
  		// add a new ParameterGroup to the database.
	 	var name = {};
	 	name[languages[0].key] = $translate.instant("new sampletype");
	 	name[languages[1].key] = "new sampletype";
	 	var position = 0;
	 	if (thisController.types){
	 		position = thisController.types.length + 1;
	 	}
	  	var newSampleType = { "name" : name };	  
	 	var promise = sampleService.addSampleType(newSampleType);
	 	promise.then(
	 		function(data) {
	 			$state.go("editSTParamGrps",{sampleTypeID:data.data.id, newSampletype:"true"}); // GOTO new sampletype
	 		},
		 	function(data) {
				console.log('error');
	 			console.log(data);
	 		}
	 	);
  	};
  	
 
  
    this.performAction = function(sampleType,action){
	  	if (action.action === "edit"){
			$state.go( "editSTParamGrps", {sampleTypeID:sampleType.id} );
	  	}
	  	if (action.action === "delete" && sampleType.deletable){
	  		var promise = sampleService.deleteSampleType(sampleType.id);
	  		promise.then(reload,error);
	  	}
    };
  
  
    
    this.update = function(parameter){
        parameter.editing = false;
        var promise = avSampleTypeService.updateSampleTypeData(
                           parameter.id, parameter) 
        promise.then(reload)
    }
    
    
  
    var reload = function() {
  	    var current = $state.current;
  	    var params = angular.copy($stateParams);
      	params.newSearch = false;
  	    return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };
    
    
    
    var error = function() {
        console.log("error");
    };
 

};

angular.module('unidaplan').controller('aSamplesController', ['$state','$stateParams',
       '$translate','restfactory','avSampleTypeService','sampleService','types','languages',aSamplesController]);

})();