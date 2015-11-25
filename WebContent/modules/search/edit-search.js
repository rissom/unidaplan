(function(){
'use strict';

function editSearchController(avParameters,restfactory,$state,$stateParams,$translate,$modal,
		key2string,sampleTypes,ptypes,search,newSearch,languages,searchService){
		
	var thisController = this;
	
	this.editFieldNL1=newSearch;
	
	this.sampleTypes=sampleTypes;
	
	this.processTypes=ptypes;
				
	this.searchTypes=[$translate.instant('Sample'),$translate.instant('Process'),$translate.instant('object specific processparameters')];
		
	this.modes=[$translate.instant("All of the following"),$translate.instant("One of the following")];
	
	this.operators = ["⋀","⋁"];
	
	this.comparators = ["<",">","=","not"];
	
	this.sampleParameters = [{name:"halli"}, {name:"hallo"}, {name:"hallo2"}]
	
	this.languages=languages;
	
	this.search=search;
	  
	this.nameL1 = search.nameL1 //parameterGrp.nameLang(languages[0].key);
	  
	this.newNameL1 = search.nameL1 //parameterGrp.nameLang(languages[0].key);
	 
	this.nameL2 = search.nameL2 //parameterGrp.nameLang(languages[1].key);

	this.newNameL2 = search.nameL2 //parameterGrp.nameLang(languages[1].key);
	    
	this.lang1=$translate.instant(languages[0].name);
	  
	this.lang2=$translate.instant(languages[1].name);
	  
	this.lang1key=$translate.instant(languages[0].key);
	  
	this.lang2key=$translate.instant(languages[1].key);
	 	  
	this.editFieldNL2=false;
	
	this.searchType=1;
		
	this.changeSampleType = function () {
		console.log ("changing sampletype");
		// Parameter laden
	}
	
	this.groups = [$translate.instant('public'),$translate.instant('only me')]
	//	this.groups += alle meine Projektgruppen


	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
		}
	}
	
	
		
	this.addSearch = function() {
		// searchService.saveSearch
	}
	
	
	
	this.edit = function(field){
		console.log("field:",field)
		thisController.editFieldNL1 = (field=="NL1");
		thisController.editFieldNL2 = (field=="NL2");
		thisController.newNameL1=thisController.nameL1;
		thisController.newNameL2=thisController.nameL2;
	}
		
	
	
	this.keyUp = function(keyCode,name,language) {
		if (keyCode===13) {				// Return key pressed
			console.log("name",name)
			console.log("language",language)
			var promise=searchService.updateSearchName(search.id,name, language);	
			promise.then(function(){reload();},function(){console.log("error")});
		}
		if (keyCode===27) {		// Escape key pressed
			  thisController.editmode=false;
		}
	}

	
	
	
	this.getSampleType = function(id) {sampleTypeID
		return sampleService.loadSample(sampleID)
	}
   
	
	
    this.addParameter = function () {
		var modalInstance = $modal.open({
			animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser.html',
		    controller: 'modalParameterChoser as mParameterChoserCtrl',
		    resolve: {
		    	mode		  	 : function(){return 'immediate'; },
		    	avParameters     : function(){return avParameters; },
				}
		});
		  
		
		
		modalInstance.result.then(
			function (result) {  // get the new Parameterlist + Info if it has changed from Modal.  
				if (result.chosen.length>0){
					var promise=avProcessTypeService.AddProcesstypePGParameters(thisController.processtype,
					  parameterGrp.id,result.chosen);
					promise.then(function(){reload();});		    	  
				}
			},function () {
				console.log('Strange Error: Modal dismissed at: ' + new Date());
		    }
		);
    };
    
    
    
    var reload=function() {
    	var current = $state.current;
    	var params = angular.copy($stateParams);
    	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    }
}  


angular.module('unidaplan').controller('editSearchController',['avParameters','restfactory','$state','$stateParams','$translate',
                          '$modal','key2string','sampleTypes','ptypes','search','newSearch','languages','searchService',editSearchController]);

})();