(function(){
'use strict';

function editSearchController(restfactory,$state,$stateParams,$translate,$modal,
		key2string,sampleTypes,ptypes,searchData,newSearch,languages,searchService,users,groups){
		
	var thisController = this;
	
	this.editFieldNL1 = newSearch;
	
	this.sampleTypes = sampleTypes;	
	
	this.sampleType = sampleTypes.filter(function(sType){return sType.id==searchData.defaultobject})[0]
	
	this.processTypes = ptypes;
	
	this.processType = ptypes.filter(function(sType){return sType.id==searchData.defaultprocess})[0]
				
	this.searchTypes = [{id:1,name:$translate.instant('Object')},
	{id:2,name:$translate.instant('Property')},
	{id:3,name:$translate.instant('object specific processparameters')}];
	
	this.searchType=searchData.type;
	
	this.mode = searchData.operation;
		
	this.modes = [{mode:true,  name:$translate.instant("All of the following")},
				  {mode:false, name:$translate.instant("One of the following")}];
					
//	this.avParameters = iSampleParamsAndGrps.parameters;
//	
//	this.paramGroups = iSampleParamsAndGrps.parametergroups;
//	
	this.languages = languages;
	
	this.search=searchData;
		
	this.output=searchData.output;
	
	this.output.sort(function(a,b){return a.position-b.position});
	  
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
	
	var allComparators = [{index:1,label:"<"},{index:2,label:">"},{index:3,label:"="},{index:4,label:"not"},{index:5,label:$translate.instant("contains")}];

	
	this.comparators={ 	integer		: allComparators.slice(0,4), 
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
				var promise=searchService.updateParameterProcessSearch(thisController.search.id,result.chosen);
				if (result.chosen.length>0){
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
    
	
	
	this.deleteParameter = function (parameter) {
		// Delete Parameter from searchcriteria
		var promise=searchService.deleteParameter(thisController.search.id,parameter.id);
		promise.then(function(){
			reload();
		});
	};
	
	
	
	this.down=function(index){
		console.log("index:"+index);
		var pos1=thisController.output[index].position;
		var pos2=thisController.output[index+1].position;
		var temp=thisController.output[index]
		var temp2=thisController.output[index+1]
		thisController.output[index]=temp2;
		thisController.output[index+1]=temp;
		thisController.output[index].position=pos1;
		console.log("pos1:"+pos1+", pos2:"+pos2);
		thisController.output[index+1].position=pos2;
		var promise = searchService.changeOrder(thisController.search.id,thisController.search.output);
		promise.then(function(){reload()},function(){console.log("error")})
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
	
	
	
	this.grantRights = function(groups,users){
		var tgroups=[];
		groups.map(function(group){tgroups.push(group.id)});		
		var tusers=[];
		users.map(function(user){tusers.push(user.id)});
		var promise = searchService.grantRights(searchData.id,tgroups,tusers)
		promise.then(function(){reload()});
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

	

	
	this.openDialog = function () {				
	    var modalInstance = $modal.open({
		    animation: false,
		    templateUrl: 'modules/modal-user-group-choser/modal-user-group-choser.html',
		    controller: 'modalUserGroupChoser as mUserGroupChoserCtrl',
		    size: 'lg',
		    resolve: {
		    	users 		: function() { return users; },
		    	chosenUsers : function() { 
		    						var cUsers=[]; 
		    						if (searchData.rights.users){
		    							for (var i=0; i<searchData.rights.users.length;i++){
		    								for (var j=0; j<users.length;j++){
		    									if (searchData.rights.users[i].id==users[j].id){
		    										cUsers.push(users[j]);
		    									}
		    								}
		    							}
		    						}
	    							return cUsers;
		    				  },
		    	chosenGroups : function() { 
									var cGroups=[]; 
									if (searchData.rights.groups){
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


	
	
	this.showParamGrp=function(parameter){
		for (var i=0;i<thisController.paramGroups.length;i++){
			if (parameter.parametergroup===thisController.paramGroups[i].id){
				return this.paramGroups[i].namef();
			}
		}
	};
	
	
	
	this.up=function(index){
		var id1=thisController.output[index-1].id;
		var id2=thisController.output[index].id;
		var pos1=thisController.output[index-1].position;
		var pos2=thisController.output[index].position;
		var temp=thisController.output[index-1]
		var temp2=thisController.output[index]
		thisController.output[index]=temp;
		thisController.output[index-1]=temp2;
		thisController.output[index-1].position=pos1;
		thisController.output[index].position=pos2;
		var promise = searchService.changeOrder(thisController.search.id,thisController.search.output);
		promise.then(function(){reload()},function(){console.log("error")})
	};
	
	
	
	this.valueKeyUp=function(keyCode,newValue,parameter){
		if (keyCode===13 || keyCode==12) {				// Return or tab key pressed
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
                          '$modal','key2string','sampleTypes','ptypes','searchData','newSearch','languages','searchService','users','groups',editSearchController]);

})();