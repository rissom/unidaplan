(function(){
'use strict';

var searchService = function (restfactory,$q,$translate,key2string,languages) {
	// restfactory is a wrapper for $html.

	var thisController=this;	
	
	
	
	this.addSearch = function(name) {
		return restfactory.POST("add-search",{"name":name});
	};
	
	
	
	this.changeComparison = function(args) {
		return restfactory.PUT("update-comparison",args);
	};
	
	
	
	this.changeMode = function(searchid,mode){
		return restfactory.PUT("update-search-operation",{searchid:searchid,operation:mode});
	}
	
	
	this.changeOwner = function(search,owner){
		return restfactory.PUT("update-search-owner",{"searchid":search.id,"newowner":owner});
	};
	
	
	
	// delete a search
	this.deleteSearch = function(search){
		return restfactory.DELETE("delete-search?searchid="+search.id);
	};
    
	
	
	// delete a parameter from search criteria
	this.deleteParameter = function(searchid,parameterid){
		return restfactory.DELETE("delete-search-parameter?parameterid="+parameterid+"&searchid="+searchid);
	};
	
	
	
	this.getSearches = function(){
		var defered=$q.defer();
		var promise = restfactory.GET("open-searches");
		promise.then(function(rest) {
		  	thisController.searches = rest.data.searches;
		  	var strings = rest.data.strings;
		  	angular.forEach(thisController.searches, function(search) {
		  		search.nameLang=function(lang){
					return key2string.key2stringWithLangStrict(search.name,strings,lang);
				};
				search.namef=function(){
					return key2string.key2string(search.name,strings);
				};
				search.actions=[{action:"edit",name:$translate.instant("edit")},
		    		            {action:"delete",name:$translate.instant("delete"),disabled:search.deletable}];
			});
			defered.resolve(thisController.searches);
		});
       return defered.promise;
	};
	
	
	
	this.getSearchData = function(searchID){
	// get the data for editing search
		var defered=$q.defer();
		var promise = restfactory.GET("search?id="+searchID);
		promise.then(function(rest) {
		  	thisController.search = rest.data.search;
		  	thisController.strings = rest.data.strings;
	  		thisController.search.namef=function(){
	  			return key2string.key2string(thisController.search.name,thisController.strings);
	  		};
	  		thisController.search.nameL1=
	  			key2string.key2stringWithLangStrict(thisController.search.name,
	  			thisController.strings,languages[0].key);
		  	thisController.search.nameL2=
		  		key2string.key2stringWithLangStrict(thisController.search.name,
		  		thisController.strings,languages[1].key);
		  	angular.forEach(thisController.search.parameter,function(parameter){
		  		parameter.namef=function(){
		  			return key2string.key2string(parameter.stringkeyname, thisController.strings);
		  		}
		  	})
		  	angular.forEach(thisController.search.output,function(parameter){
		  		parameter.namef=function(){
		  			return key2string.key2string(parameter.stringkeyname, thisController.strings);
		  		}
		  	})
		  	if (thisController.search.type===1){
		  		var prom2 = thisController.getSParameters(thisController.search.defaultobject);
		  		prom2.then(function(){
					defered.resolve(thisController.search);
		  		});
		  	}
		  	if (thisController.search.type===2){
		  		var prom2 = thisController.getPParameters(thisController.search.defaultobject);
		  		prom2.then(function(){
					defered.resolve(thisController.search);
		  		});
		  	}
		  	if (thisController.search.type===3){
		  		var prom2 = thisController.getPParameters(thisController.search.defaultobject);
		  		prom2.then(function(){
					defered.resolve(thisController.search);
		  		});
		  	}
		});
       return defered.promise;
	};
	
	
	
	this.getSearch = function(searchID){
		//get search for executing search
		var defered=$q.defer();
		var promise = restfactory.GET("search?id="+searchID);
		promise.then(function(rest) {
		  	thisController.search = rest.data.search;
		  	var strings = rest.data.strings;
	  		thisController.search.namef=function(){
	  			return key2string.key2string(thisController.search.name,strings);
	  		};
	  		thisController.search.nameL1=
	  			key2string.key2stringWithLangStrict(thisController.search.name,
	  			strings,languages[0].key);
		  	thisController.search.nameL2=
		  		key2string.key2stringWithLangStrict(thisController.search.name,
		  		languages[1].key);
		  	angular.forEach (thisController.search.parameter,function(parameter){
		  		parameter.fname=function(){
		  			return key2string.key2string(parameter.stringkeyname,strings);
		  		}
		  	});
			defered.resolve(thisController.search);
		});
       return defered.promise;
	};
	
	
	
	this.getSParameters = function(sampleType){
		var defered=$q.defer();
		var promise = restfactory.GET('/all-sample-type-params'+"?sampletypeid="+sampleType);
		promise.then(function(rest) {
		  	thisController.search.avParameters = rest.data.parameters;
		  	thisController.search.avParamGrps = rest.data.parametergrps;
		  	var strings = rest.data.strings;
		  	angular.forEach (thisController.search.avParameters,function(parameter){
		  		parameter.namef=function(){
		  			return key2string.key2string(parameter.name,strings);
		  		}
		  	});
		  	angular.forEach (thisController.search.avParamGrps,function(paramGrp){
		  		paramGrp.namef=function(){
		  			return key2string.key2string(paramGrp.stringkey,strings);
		  		}
		  	});
			defered.resolve(thisController.search.parameters);
		});
       return defered.promise;
	}
	
	
	
	this.getPParameters = function(processType){
		var defered=$q.defer();
		var promise = restfactory.GET('/all-process-type-params'+"?processtypeid="+processType);
		promise.then(function(rest) {
		  	thisController.search.avParameters = rest.data.parameters;
		  	thisController.search.avParamGrps = rest.data.parametergrps;
		  	var strings = rest.data.strings;
		  	angular.forEach (thisController.search.avParameters,function(parameter){
		  		parameter.namef=function(){
		  			return key2string.key2string(parameter.name,strings);
		  		}
		  	});
		  	angular.forEach (thisController.search.avParamGrps,function(paramGrp){
		  		paramGrp.namef=function(){
		  			return key2string.key2string(paramGrp.stringkey,strings);
		  		}
		  	});
			defered.resolve(thisController.search.parameters);
		});
       return defered.promise;
	}
	
	
	this.startSearch = function (searchparameters){
		var defered=$q.defer();
		var promise = restfactory.POST("result",searchparameters);
		promise.then(function(rest) {
			thisController.result = rest.data;
			angular.forEach(thisController.result.headings,function(heading){
				heading.namef=function(){
					return key2string.key2string(heading.stringkeyname,rest.data.strings);
				}
				if (heading.stringekeyunit){
					heading.unitf=function(){
						return key2string.key2string(heading.stringkeyunit,rest.data.strings);
					}
				}
			});
			defered.resolve(thisController.result);
		});
	    return defered.promise;
	}
	

	
	this.updateSearchOutput = function(id,parameters){
		return restfactory.PUT("update-search-output",{searchid:id,output:parameters});
	};
	
	
	
	this.updateSearchParamValue = function(searchid,parameter,newValue){
		return restfactory.PUT("update-search-param-value",{searchid:searchid,pid:parameter,value:newValue});
	}
	
	
	this.updateParameterSampleSearch = function(searchid,otparameter){
		return restfactory.POST("update-param-sample-search",{searchid:searchid,otparameter:otparameter});
	}
	
	
	this.updateSearchName = function(searchID,name,language){
		var promise = restfactory.PUT("update-search-name",{searchID:searchID,newname:name,language:language});
		return promise;
	};
		
};


angular.module('unidaplan').service('searchService', ['restfactory','$q','$translate',
               'key2string','languages',searchService]);

})();