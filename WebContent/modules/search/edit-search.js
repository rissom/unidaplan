(function(){
'use strict';

function editSearchController(restfactory,$filter,$state,$stateParams,$translate,$uibModal,
		key2string,sampleTypes,ptypes,searchData,newSearch,languages,searchService,users,groups){
		
	var thisController = this;
	
	this.editFieldNL1 = newSearch;
	
	this.sampleTypes = sampleTypes;	
		
	if (searchData.defaultobject) {
		this.sampleType = sampleTypes.filter(function(sType){return sType.id==searchData.defaultobject})[0]	
	} else {
		this.sampleType = sampleTypes[0];
	}
	
	this.processTypes = ptypes;
	
	if (searchData.defaultprocess){
		this.processType = ptypes.filter(function(sType){return sType.id==searchData.defaultprocess})[0]
	} else{
		this.processType = ptypes[0];
	}
				
	this.searchTypes = [{id:1,name:$translate.instant('sample')},
	{id:2,name:$translate.instant('property')},
	{id:3,name:$translate.instant('sample specific processparameters')},
	{id:4,name:$translate.instant('sample/process')}];

	
	this.searchType = searchData.type;
	
	this.mode = searchData.operation;
		
	this.modes = [{mode:true,  name:$translate.instant("All of the following")},
				  {mode:false, name:$translate.instant("One of the following")}];
					
//	this.avParameters = iSampleParamsAndGrps.parameters;
//	
//	this.paramGroups = iSampleParamsAndGrps.parametergroups;
//	
	this.languages = languages;
	
	this.search = searchData;
		
	this.ooutput=$filter('filter')(searchData.output,{type:'o'});
	
	this.poutput=$filter('filter')(searchData.output,{type:'p'});
	
	this.pooutput=$filter('filter')(searchData.output,{type:'po'});
		
	this.poutput.sort(function(a,b){return a.position-b.position});
	
	this.ooutput.sort(function(a,b){return a.position-b.position});

	this.poutput.sort(function(a,b){return a.position-b.position});
	  
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

	this.users = users;
	
	angular.forEach(this.search.rights.groups,function(group){ // attach names to groups
		group.name = groups.filter(function(gr){return gr.id == group.id})[0].name;
	});
	
	angular.forEach(this.search.rights.users,function(user){ // attach names to users
		user.name = users.filter(function(usr){return usr.id == user.id})[0].fullname;
	});
	
	var allComparators = [{index:0, label:"<"},
	                      {index:1, label:"<="},
	                      {index:2, label:"="},
	                      {index:3, label:">="},
	                      {index:4, label:">"},
	                      {index:5, label:"!="},
	                      {index:6, label:$translate.instant("contains")}];
	

	
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
					
	
	
	this.addSampleParameter=function(){
		var modalInstance = $uibModal.open({
			animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser-with-grps.html',
		    controller: 'modalParameterChoserGrps as mParameterChoserGrpsCtrl',
		    resolve: {
		    	mode		  	 : function(){return 'immediate'; },
		    	avParameters     : function(){return thisController.search.avSParameters; },
		    	paramGroups      : function(){return thisController.search.avSParamGrps; },
		    	parameters		 : function(){return []}
				}
		});
		
		modalInstance.result.then(
			function (result) {  // get the new Parameterlist + Info if it has changed from Modal.  
				if (result.chosen.length>0){
					var promise=searchService.updateSearchSParameter(thisController.search.id,result.chosen);
					promise.then(function(){reload();});		    	  
				}
			},function () {
				console.log('Strange Error: Modal dismissed at: ' + new Date());
		    }
		);
	}
	

	
	this.addProcessParameter=function(){
		var modalInstance = $uibModal.open({
			animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser-with-grps.html',
		    controller: 'modalParameterChoserGrps as mParameterChoserGrpsCtrl',
		    resolve: {
		    	mode		  	 : function(){return 'immediate'; },
		    	avParameters     : function(){return thisController.search.avPParameters; },
		    	paramGroups      : function(){return thisController.search.avPParamGrps; },
		    	parameters		 : function(){return []}
				}
		});
		  	
		modalInstance.result.then(
			function (result) {  // get the new Parameterlist + Info if it has changed from Modal.  
				var promise=searchService.updateSearchPParameter(thisController.search.id,result.chosen);
				if (result.chosen.length>0){
					promise.then(function(){reload();});		    	  
				}
			},function () {
				console.log('Strange Error: Modal dismissed at: ' + new Date());
		    }
		);
	}
	
	
	
    this.addOutputParameter = function (type) {
		var modalInstance = $uibModal.open({
			animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser-with-grps.html',
		    controller: 'modalParameterChoserGrps as mParameterChoserGrpsCtrl',
		    resolve: {
		    	mode		  	 : function(){return 'immediate'; },
		    	avParameters     : function(){ // All parameters which have not been already chosen
		    		var avParams=[];
		    		var outParams=[];
		    		var oparameters=[];
		    		switch (type){
			    		case "o" : avParams=searchData.avSParameters; outParams=thisController.ooutput; break;
			    		case "p" : avParams=searchData.avPParameters; outParams=thisController.poutput; break;
			    		case "po": avParams=searchData.avPOParameters; outParams=thisController.pooutput;
		    		}
		    		for (var i=0; i<outParams.length;i++){ 
		    			oparameters.push(outParams[i].id);
		    		}
			    	return $filter('filter')(avParams,function(p){
			    		return (oparameters.indexOf(p.id))==-1});} ,
		    	paramGroups      : function(){
		    		var avParamGrps=[];
		    		switch (type){
			    		case "o" : avParamGrps=searchData.avSParamGrps; break;
			    		case "p" : avParamGrps=searchData.avPParamGrps; break;
			    		case "po": avParamGrps=searchData.avPOParamGrps;
		    		}
		    		return avParamGrps; },
		    	parameters    	 : function(){
		    			// make an array of outputparameterids of the given type
			    		var oparameters=[];
			    		var outParams=[];
			    		switch (type){
				    		case "o" : outParams=thisController.ooutput; break;
				    		case "p" : outParams=thisController.poutput; break;
				    		case "po" : outParams=thisController.pooutput;
			    		}
			    		for (var i=0; i<outParams.length;i++){ 
			    			oparameters.push(outParams[i].id);
			    		}
			    		return oparameters;
	//		    		return searchData.output.reduce(function(x,y){return x.push(y.id);},[]); // not working! Why???
		    		}
				}
		});
		  	
		modalInstance.result.then(
			function (result) {  // get the new Parameterlist + Info if it has changed from Modal.  
				if (result.chosen.length>0){
					var newOutParams=result.inParams;
					angular.forEach (result.chosen,
						function(p){
							if (newOutParams.indexOf(p)==-1) newOutParams.push(p)
						}
					);
					var promise=searchService.updateSearchOutput(thisController.search.id,newOutParams,type);
					promise.then(function(){reload();});
				}
			},function () {
				console.log('Strange Error: Modal dismissed at: ' + new Date());
		    }
		);
    };
		
	
	
	this.changeComparison = function(parameter,type){
		var args = {searchid:searchData.id,id:parameter.id,comparison:parameter.comparison,type:type};
		var promise = searchService.changeComparison(args);
		promise.then(function(){reload();},function(){console.log("Error")});
	}
	
	
	
	this.changeMode = function(){
		var promise = searchService.changeMode(searchData.id,this.mode==1);
		promise.then(function(){reload();},function(){console.log("Error")});
	}
	
	
		
	this.changeOwner = function() {
		var promise = searchService.changeOwner(searchData,thisController.newOwner);
		promise.then(function(){
			reload();
		},function(){
			console.log("error");
			thisController.editOwner = false;
		});
	};



	this.changeSampleType = function () {
		thisController.search.defaultobject=thisController.sampleType.id
		var promise=searchService.getSParameters(thisController.search);
		promise.then(function(rest){
			thisController.avParameters=rest;
		});
	};
	
	
	
	this.changeProcessType = function () {
		thisController.search.defaultprocess=thisController.processType.id
		var promise=searchService.getPParameters(thisController.search);
		promise.then(function(rest){
			thisController.avParameters=rest;
		});
	};
	
	
	
	this.changeType = function(){
		var promise = searchService.updateSearchType(thisController.search.id,thisController.searchType);
		promise.then(reload());
	};
	
	
    
    this.deleteOutParameter = function(parameter,type){
		var oparameters=[];
		var tParams=[];
		switch (type) {
		case 'o'  : tParams=thisController.ooutput; break;
		case 'p'  : tParams=thisController.poutput; break;
		case 'po' : tParams=thisController.pooutput; 
		}
		for (var i=0; i<tParams.length;i++){ 
			if (tParams[i].id!=parameter.id){
				oparameters.push(tParams[i].id);
			}
		}
		var promise=searchService.updateSearchOutput(thisController.search.id,oparameters,type);
		promise.then(function(){reload();});		
    }
    
	
	
	this.deleteParameter = function (parameter,type) {
		// Delete Parameter from searchcriteria
		var promise=searchService.deleteParameter(thisController.search.id,parameter.id,type);
		promise.then(function(){
			reload();
		});
	};
	
	
	var exchangePositions = function(array, index1,index2){
		var pos1=array[index1].position;
		var pos2=array[index2].position;
		array[index1].position=pos2;
		array[index2].position=pos1;
		array.sort(function(a,b){return a.position-b.position});
	}
	
	
	this.down = function(index,type){
		if (type=='o'){
			exchangePositions (thisController.ooutput, index, index+1);
			var promise = searchService.changeOrder(thisController.search.id,thisController.ooutput,type);
			promise.then(function(){reload()},function(){console.log("error")})
		} else {
			exchangePositions (thisController.poutput, index, index+1);
			var promise = searchService.changeOrder(thisController.search.id,thisController.poutput,type);
			promise.then(function(){reload()},function(){console.log("error")})
		}
	};
	
	
	
	this.edit = function(field){
		thisController.editFieldNL1 = (field=="NL1");
		thisController.editFieldNL2 = (field=="NL2");
		thisController.editOwner = (field=="owner");
		thisController.newNameL1 = thisController.nameL1;
		thisController.newNameL2 = thisController.nameL2;
	};
		
	
	
	this.getSampleType = function(id) {
		return sampleService.loadSample(sampleID)
	};
    

	
	this.getOwner = function(){
		var username = "unknown";
		angular.forEach(users,function(user){
			if (user.id == searchData.owner){ 
				username=user.fullname;
			}
		});
		return username;
	};
	
	
	
	this.grantRights = function(groups,users){
		var tgroups=[];
		groups.map(function(group){tgroups.push(group.id)});		
		var tusers=[];
		users.map(function(user){tusers.push(user.id)});
		var promise = searchService.grantRights(searchData.id,tgroups,tusers)
		promise.then(function(){reload()});
	};
	
	
	
	this.keyUp = function(keyCode,name,language) {
		if (keyCode === 13) {				// Return key pressed
			var promise=searchService.updateSearchName(searchData.id,name, language);	
			promise.then(function(){
				reload();
			},function(){
				console.log("error");
			});
		}
		if (keyCode === 27) {		// Escape key pressed
			  thisController.editmode=false;
		}
	};

	

	
	this.openDialog = function () {				
	    var modalInstance = $uibModal.open({
		    animation: false,
		    templateUrl: 'modules/modal-user-group-choser/modal-user-group-choser.html',
		    controller: 'modalUserGroupChoser as mUserGroupChoserCtrl',
		    size: 'lg',
		    resolve: {
		    	users 		: function() { return users; },
		    	chosenUsers : function() { 
		    						var cUsers = []; 
		    						if (searchData.rights && searchData.rights.users){
		    							for (var i = 0; i < searchData.rights.users.length; i++){
		    								for (var j = 0; j < users.length; j++){
		    									if (searchData.rights.users[i].id == users[j].id){
		    										cUsers.push(users[j]);
		    									}
		    								}
		    							}
		    						}
		    						console.log(cUsers)
	    							return cUsers;
		    				  },
		    	chosenGroups : function() { 
									var cGroups=[]; 
									if (searchData.rights && searchData.rights.groups){
		    							for (var i=0; i<searchData.rights.groups.length;i++){
		    								for (var j=0; j<groups.length;j++){
		    									if (searchData.rights.groups[i].id==groups[j].id){
		    										cGroups.push(groups[j]);
		    									}
		    								}
		    							}
		    						}
									return cGroups;
							  },	  
		        groups      : function() { 
		        				return groups;
		        			  },
		        except		: function() {
//		        				var eSamples2=eSamples.slice(0);
//		        				eSamples2.push({sampleid:sample.id,typeid:sample.typeid,name:sample.name});
//		        				return eSamples2;
		        				return [];
		        				},
		        buttonLabel	: function() { return "add to search"; },
		        label		: function() { return "grant rights";}
		    }		        
		});
	    
	  	modalInstance.result.then(function (result) {  // get the new Userlist + Info if it is changed from Modal. 
			if (result.changed==true){
				thisController.grantRights(result.chosenGroups,result.chosenUsers);
			}
	    }, function () {
	        console.log('Strange Error: Modal dismissed at: ' + new Date());
	    });
	};


	
	
	this.showParamGrp = function(parameter){
		for (var i = 0; i < thisController.paramGroups.length; i++){
			if (parameter.parametergroup === thisController.paramGroups[i].id){
				return this.paramGroups[i].namef();
			}
		}
	};
	

	
	
	this.up = function(index,type){
		if (type === 'o'){
			exchangePositions(thisController.ooutput,index-1,index);
			var promise = searchService.changeOrder(thisController.search.id,thisController.ooutput,type);
			promise.then(function(){reload()},function(){console.log("error")})
		}else{
			exchangePositions(thisController.poutput,index-1,index);
			var promise = searchService.changeOrder(thisController.search.id,thisController.poutput,type);
			promise.then(function(){reload()},function(){console.log("error")})
		}		

	};
	
	
	
	this.valueKeyUp = function(keyCode,newValue,parameter,type){
		if (keyCode === 13 || keyCode === 12) {				// Return or tab key pressed
			var promise = searchService.updateSearchParamValue(thisController.search.id,parameter.id,newValue,type);
			promise.then( function(){ reload(); } );
		}
	}
    
    
    var reload = function() {
    	var current = $state.current;
    	var params = angular.copy($stateParams);
    	params.newSearch = false;
    	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };
}  


angular.module('unidaplan').controller('editSearchController',['restfactory','$filter','$state','$stateParams','$translate',
                          '$uibModal','key2string','sampleTypes','ptypes','searchData','newSearch','languages','searchService','users','groups',editSearchController]);

})();