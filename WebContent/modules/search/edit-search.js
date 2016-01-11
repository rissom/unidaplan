(function(){
'use strict';

function editSearchController(restfactory,$state,$stateParams,$translate,$modal,
		key2string,sampleTypes,ptypes,searchData,newSearch,languages,searchService,users){
		
	var thisController = this;
	
	this.editFieldNL1 = newSearch;
	
	this.sampleTypes = sampleTypes;
	
	
	this.sampleType=sampleTypes.filter(function(sType){return sType.id==searchData.defaultobject})[0]
	
	this.processTypes = ptypes;
				
	this.searchTypes = [{id:1,name:$translate.instant('Sample')},
	{id:2,name:$translate.instant('Process')},
	{id:3,name:$translate.instant('object specific processparameters')}];
	
	this.searchType=searchData.type;
	
	this.mode = searchData.operation;
		
	this.modes = [{mode:true,  name:$translate.instant("All of the following")},
				  {mode:false, name:$translate.instant("One of the following")}];
				
	this.sampleParameters = [{name:"halli"}, {name:"hallo"}, {name:"hallo2"}];
	
//	this.avParameters = iSampleParamsAndGrps.parameters;
//	
//	this.paramGroups = iSampleParamsAndGrps.parametergroups;
//	
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

	this.groups = [$translate.instant('public'),$translate.instant('only me')];
	//	this.groups += alle meine Projektgruppen
	
	this.users = users;

	
	var allComparators= [{index:1,label:"<"},{index:2,label:">"},{index:3,label:"="},{index:4,label:"not"}];

	this.comparators={ 	integer		: allComparators, 
 						float 		: allComparators,
			 			measurement : allComparators,
			 			string		: allComparators.slice(2),
			 			longstring	: allComparators.slice(2),
			 			chooser		: allComparators.slice(2),
			 			date		: allComparators,
			 			checkbox	: allComparators.slice(2),
			 			timestamp	: allComparators,
			 			URL			: allComparators.slice(2),
			 			email		: allComparators.slice(2)
					};
					
	
	
	this.addSampleParameter=function(){
		var modalInstance = $modal.open({
			animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser-with-grps.html',
		    controller: 'modalParameterChoserGrps as mParameterChoserGrpsCtrl',
		    resolve: {
		    	mode		  	 : function(){return 'immediate'; },
		    	avParameters     : function(){return thisController.search.avParameters; },
		    	paramGroups      : function(){return thisController.search.avParamGrps; },
		    	parameters		 : function(){return []}
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
	

	
	this.addProcessParameter=function(){
		var modalInstance = $modal.open({
			animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser-with-grps.html',
		    controller: 'modalParameterChoserGrps as mParameterChoserGrpsCtrl',
		    resolve: {
		    	mode		  	 : function(){return 'immediate'; },
		    	avParameters     : function(){return thisController.search.avParameters; },
		    	paramGroups      : function(){return thisController.search.avParamGrps; },
		    	parameters		 : function(){return []}
				}
		});
		  	
		modalInstance.result.then(
			function (result) {  // get the new Parameterlist + Info if it has changed from Modal.  
				if (result.chosen.length>0){
					var promise=searchService.updateParameterProcessSearch(thisController.search.id,result.chosen);
					promise.then(function(){reload();});		    	  
				}
			},function () {
				console.log('Strange Error: Modal dismissed at: ' + new Date());
		    }
		);
	}
	
	
	
    this.addOutputParameter = function () {
		var modalInstance = $modal.open({
			animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser-with-grps.html',
		    controller: 'modalParameterChoserGrps as mParameterChoserGrpsCtrl',
		    resolve: {
		    	mode		  	 : function(){return 'immediate'; },
		    	avParameters     : function(){return thisController.search.avParameters; },
		    	paramGroups      : function(){return thisController.search.avParamGrps; },
		    	parameters    	 : function(){
			    		var oparameters=[];
			    		for (var i=0; i<searchData.output.length;i++){ 
			    			oparameters.push(searchData.output[i].id);
			    		}
			    		return oparameters;
	//		    		return searchData.output.reduce(function(x,y){return x.push(y.id);},[]); // not working! Why???
		    		}
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
    
    
    this.deleteOutParameter = function(parameter){
		var oparameters=[];
		for (var i=0; i<searchData.output.length;i++){ 
			if (searchData.output[i].id!=parameter.id){
				oparameters.push(searchData.output[i].id);
			}
		}
		var promise=searchService.updateSearchOutput(thisController.search.id,oparameters);
		promise.then(function(){reload();});		
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
		var promise=searchService.getSParameters(this.sampleType.id);
		promise.then(function(rest){
			thisController.avParameters=rest;
		});
	};
	
	
	
	this.deleteParameter = function (parameter) {
		// Delete Parameter from searchcriteria
		var promise=searchService.deleteParameter(thisController.search.id,parameter.id);
		promise.then(function(){
			reload();
		});
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


angular.module('unidaplan').controller('editSearchController',['restfactory','$state','$stateParams','$translate',
                          '$modal','key2string','sampleTypes','ptypes','searchData','newSearch','languages','searchService','users',editSearchController]);

})();