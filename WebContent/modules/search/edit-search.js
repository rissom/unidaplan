(function(){
'use strict';

function editSearchController(iSampleParamsAndGrps,restfactory,$state,$stateParams,$translate,$modal,
		key2string,sampleTypes,ptypes,searchData,newSearch,languages,searchService,users){
		
	var thisController = this;
	
	this.editFieldNL1 = newSearch;
	
	this.sampleTypes = sampleTypes;
	
	this.processTypes = ptypes;
				
	this.searchTypes = [$translate.instant('Sample'),$translate.instant('Process'),$translate.instant('object specific processparameters')];
	
	this.mode = searchData.operation;
		
	this.modes = [{mode:true,  name:$translate.instant("All of the following")},
				  {mode:false, name:$translate.instant("One of the following")}];
			
	this.comparators = [{index:1,label:"<"},{index:2,label:">"},{index:3,label:"="},{index:4,label:"not"}];
	
	this.sampleParameters = [{name:"halli"}, {name:"hallo"}, {name:"hallo2"}];
	
	this.avParameters = iSampleParamsAndGrps.parameters;
	
	this.paramGroups = iSampleParamsAndGrps.parametergroups;
	
	this.languages = languages;
	
	this.search=searchData;
	  
	this.nameL1 = searchData.nameL1; //parameterGrp.nameLang(languages[0].key);
	  
	this.newNameL1 = searchData.nameL1; //parameterGrp.nameLang(languages[0].key);
	 
	this.nameL2 = searchData.nameL2; //parameterGrp.nameLang(languages[1].key);

	this.newNameL2 = searchData.nameL2; //parameterGrp.nameLang(languages[1].key);
	
	this.newOwner = searchData.owner;
	    
	this.lang1 = $translate.instant(languages[0].name);
	  
	this.lang2 = $translate.instant(languages[1].name);
	  
	this.lang1key = $translate.instant(languages[0].key);
	  
	this.lang2key = $translate.instant(languages[1].key);
	 	  
	this.editFieldNL2 = false;
	
	this.searchType=1;
		
	this.groups = [$translate.instant('public'),$translate.instant('only me')];
	//	this.groups += alle meine Projektgruppen
	
	this.users = users;

	
	
	this.addSampleParameter=function(){
		var modalInstance = $modal.open({
			animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser-with-grps.html',
		    controller: 'modalParameterChoserGrps as mParameterChoserGrpsCtrl',
		    resolve: {
		    	mode		  	 : function(){return 'immediate'; },
		    	avParameters     : function(){return thisController.avParameters; },
		    	paramGroups      : function(){return thisController.paramGroups; },
				}
		});
		  	
		modalInstance.result.then(
			function (result) {  // get the new Parameterlist + Info if it has changed from Modal.  
				if (result.chosen.length>0){
					var promise=searchService.updateParameterSampleSearch(thisController.search.id,result.chosen);
					promise.then(function(){reload();});		    	  
				}
			},function () {
				console.log('Strange Error: Modal dismissed at: ' + new Date());
		    }
		);
	}
	

	
    this.addParameter = function () {
		var modalInstance = $modal.open({
			animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser-with-grps.html',
		    controller: 'modalParameterChoserGrps as mParameterChoserGrpsCtrl',
		    resolve: {
		    	mode		  	 : function(){return 'immediate'; },
		    	avParameters     : function(){return thisController.avParameters; },
		    	paramGroups      : function(){return thisController.paramGroups; },
		    	parameters    	 : function(){
		    		var oparameters=[];
		    		for (var i=0; i<searchData.output.length;i++){ 
		    			oparameters.push(searchData.output[i].id);
		    		}
		    		return oparameters
//		    		return searchData.output.reduce(function(x,y){return x.push(y.id);},[]); // not working! Why???
		    		},
				}
		});
		  	
		modalInstance.result.then(
			function (result) {  // get the new Parameterlist + Info if it has changed from Modal.  
				if (result.chosen.length>0){
					var promise=searchService.updateSearchOutput(thisController.search.id,result.chosen);
					promise.then(function(){reload();});		    	  
				}
			},function () {
				console.log('Strange Error: Modal dismissed at: ' + new Date());
		    }
		);
    };
    
    
    
    this.addSampleParameter = function(){
    }
    
    
	
	this.addSearch = function() {
		// searchService.saveSearch
	};
	
	
	
	this.allowedUsers = ["Greti", "Pleti"];
	
	
	
	this.changeComparison = function(parameter){
		var args = {searchid:searchData.id,id:parameter.id,comparison:parameter.comparison};
		var promise = searchService.changeComparison(args);
		promise.then(function(){reload();},function(){console.log("Error")});
	}
	
	
	
	this.changeMode = function(){
		var promise = searchService.changeMode(searchData.id,this.mode==1);
		promise.then(function(){reload();},function(){console.log("Error")});
	}
	
	
		
	this.changeOwner = function() {
		var promise=searchService.changeOwner(searchData,thisController.newOwner);
		promise.then(function(){
			reload();
		},function(){
			console.log("error");
			thisController.editOwner=false;
		});
	};
	
	
	
	this.changeSampleType = function () {
		console.log ("changing sampletype");
		var promise=searchService.getSParameters(this.sampleType);
		promise.then(function(params){
			this.avParameters=params;
		});
		// Parameter laden
	};
	
	
	
	this.edit = function(field){
		thisController.editFieldNL1 = (field=="NL1");
		thisController.editFieldNL2 = (field=="NL2");
		thisController.editOwner = (field=="owner");
		thisController.newNameL1=thisController.nameL1;
		thisController.newNameL2=thisController.nameL2;
	};
		
	
	
	this.getSampleType = function(id) {
		return sampleService.loadSample(sampleID)
	};
    

	
	this.getOwner = function(){
		var username="unknown";
		angular.forEach(users,function(user){
			if (user.id==searchData.owner){ 
				username=user.fullname;
			}
		});
		return username;
	};
	
	
	
	this.keyUp = function(keyCode,name,language) {
		if (keyCode===13) {				// Return key pressed
			var promise=searchService.updateSearchName(searchData.id,name, language);	
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

	
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			console.log ("hallo")
		}
	};
	
	
	
	this.showParamGrp=function(parameter){
		for (var i=0;i<thisController.paramGroups.length;i++){
			if (parameter.parametergroup===thisController.paramGroups[i].id){
				return this.paramGroups[i].namef();
			}
		}
	};
	
	
	
	this.valueKeyUp=function(keyCode,newValue,parameter){
		if (keyCode===13) {				// Return key pressed
			var promise = searchService.updateSearchParamValue(thisController.search.id,parameter.id,newValue);
			promise.then(function(){reload();});
		}
	}
    
    
    var reload=function() {
    	var current = $state.current;
    	var params = angular.copy($stateParams);
    	params.newSearch=false;
    	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };
}  


angular.module('unidaplan').controller('editSearchController',['iSampleParamsAndGrps','restfactory','$state','$stateParams','$translate',
                          '$modal','key2string','sampleTypes','ptypes','searchData','newSearch','languages','searchService','users',editSearchController]);

})();