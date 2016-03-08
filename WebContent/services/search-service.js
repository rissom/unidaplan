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
	
	
	this.changeOrder = function (searchID,output,type){
		var newOrder = { searchid : searchID,
			  		 	 output   : output,
			  		 	 type 	  : type};
		return restfactory.PUT('change-order-search-output',newOrder);
	};
		
		
	// delete a search
	this.deleteSearch = function(search){
		return restfactory.DELETE("delete-search?searchid="+search.id);
	};
	
	
	
	// delete a parameter from search criteria
	this.deleteParameter = function(searchid,parameterid,type){
		return restfactory.DELETE("delete-search-parameter?parameterid="+parameterid+"&searchid="+searchid+"&type="+type);
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
		var promise = restfactory.GET("searchdata?id="+searchID);
		promise.then(function(rest) {
		  	var search = rest.data.search;
		  	var strings = rest.data.strings;
	  		search.namef=function(){
	  			return key2string.key2string(search.name,strings);
	  		};
	  		search.nameL1=
	  			key2string.key2stringWithLangStrict(search.name,
	  			strings,languages[0].key);
		  	search.nameL2=
		  		key2string.key2stringWithLangStrict(search.name,
		  		strings,languages[1].key);
		  	if (search.sparameter){
			  	angular.forEach(search.sparameter,function(parameter){
			  		parameter.namef=function(){
			  			return key2string.key2string(parameter.stringkeyname, strings);
			  		}
			  	})
		  	}
		  	if (search.pparameter){
			  	angular.forEach (search.pparameter,function(parameter){
			  		parameter.namef=function(){
			  			return key2string.key2string(parameter.stringkeyname,strings);
			  		}
			  	});
		  	}
		  	if (search.poparameter){
			  	angular.forEach (search.poparameter,function(parameter){
			  		parameter.fname=function(){
			  			return key2string.key2string(parameter.stringkeyname,strings);
			  		}
			  	});
		  	}
		  	angular.forEach(search.output,function(parameter){
		  		parameter.namef=function(){
		  			return key2string.key2string(parameter.stringkeyname, strings);
		  		}
		  	})
		  	if (search.type===1){
		  		var prom2 = thisController.getSParameters(search);
		  		prom2.then(function(){
					defered.resolve(search);
		  		});
		  	}
		  	if (search.type===2){
		  		var prom2 = thisController.getPParameters(search);
		  		prom2.then(function(){
					defered.resolve(search);
		  		});
		  	}
		  	if (search.type===3){
		  		var prom2 = thisController.getPParameters(search);
		  		prom2.then(function(){
					defered.resolve(search);
		  		});
		  	}
		  	if (search.type===4){
		  		var prom2 = thisController.getSParameters(search);
		  		prom2.then(function(){
					defered.resolve(search);
		  		});
		  		var prom3 = thisController.getPParameters(search);
		  		prom3.then(function(){
					defered.resolve(search);
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
		  	var search = rest.data.search;
		  	var strings = rest.data.strings;
	  		search.namef=function(){
	  			return key2string.key2string(search.name,strings);
	  		};
	  		search.nameL1=
	  			key2string.key2stringWithLangStrict(search.name,
	  			strings,languages[0].key);
		  	search.nameL2=
		  		key2string.key2stringWithLangStrict(search.name,
		  		languages[1].key);
		  	if (search.sparameters){
			  	angular.forEach (search.sparameters,function(parameter){
			  		parameter.namef=function(){
			  			return key2string.key2string(parameter.stringkeyname,strings);
			  		}
			  	});
		  	}
		  	if (search.pparameters){
			  	angular.forEach (search.pparameters,function(parameter){
			  		parameter.namef=function(){
			  			return key2string.key2string(parameter.stringkeyname,strings);
			  		}
			  	});
		  	}
		  	if (search.poparameters){
			  	angular.forEach (search.poparameters,function(parameter){
			  		parameter.fname=function(){
			  			return key2string.key2string(parameter.stringkeyname,strings);
			  		}
			  	});
		  	}
			defered.resolve(search);
		});
       return defered.promise;
	};
	
	
	
	this.getSParameters = function(search){
		var defered=$q.defer();
		var promise = restfactory.GET('/all-sample-type-params'+"?sampletypeid="+search.defaultobject);
		promise.then(function(rest) {
			angular.forEach(rest.data.parameters,function(parameter){parameter.type="o";}) // add type to all parameters
		  	search.avSParameters = rest.data.parameters;
		  	search.avSParamGrps = rest.data.parametergrps;
		  	var strings = rest.data.strings;
		  	angular.forEach (search.avSParameters,function(parameter){
		  		parameter.namef=function(){
		  			return key2string.key2string(parameter.name,strings);
		  		}
		  	});
		  	angular.forEach (search.avSParamGrps,function(paramGrp){
		  		paramGrp.namef=function(){
		  			return key2string.key2string(paramGrp.stringkey,strings);
		  		}
		  	});
			defered.resolve(search.parameters);
		});
       return defered.promise;
	}
	
	
	
	this.getPParameters = function(search){
		var defered=$q.defer();
		var url="/all-process-type-params";
		if (search.defaultprocess){
			url+="?processtypeid="+search.defaultprocess;
		}
		var promise = restfactory.GET(url);
		promise.then(function(rest) {
		  	search.avPParameters = rest.data.parameters;
		  	search.avPParamGrps = rest.data.parametergrps;
		  	var strings = rest.data.strings;
		  	angular.forEach (search.avPParameters,function(parameter){
		  		parameter.namef=function(){
		  			return key2string.key2string(parameter.name,strings);
		  		}
		  	});
		  	angular.forEach (search.avPParamGrps,function(paramGrp){
		  		paramGrp.namef=function(){
		  			return key2string.key2string(paramGrp.stringkey,strings);
		  		}
		  	});
		  	if (rest.data.processtype){
		  		search.processtype=rest.data.processtype;
		  	}
			defered.resolve(search.parameters);
		});
       return defered.promise;
	}
	
	
	
	this.grantRights = function (searchID,groups,users){
		return restfactory.PUT("update-search-rights",{searchid:searchID,groups:groups,users:users});
	}
	
	
	
	this.startSearch = function (searchparameters){
		var defered=$q.defer();
		if (searchparameters.searchtype<4){
			var promise = restfactory.POST("result",searchparameters);
			promise.then(function(rest) {
				var result = rest.data;
				var strings = rest.data.strings;
				angular.forEach(result.headings,function(heading){
					heading.namef=function(){
						return key2string.key2string(heading.stringkeyname,strings);
					}
					if (heading.stringkeyunit){
						heading.unitf=function(){
							return key2string.key2string(heading.stringkeyunit,strings);
						}
					}
				});
				defered.resolve(result);
			});
		} else {
			var promise = restfactory.POST("result-type4",searchparameters);
			promise.then(function(rest) {
				var result = rest.data;
				var strings = rest.data.strings;
				angular.forEach(result.headings,function(heading){
					heading.namef=function(){
						return key2string.key2string(heading.stringkeyname,strings);
					}
					if (heading.stringkeyunit){
						heading.unitf=function(){
							return key2string.key2string(heading.stringkeyunit,strings);
						}
					}
				});
				delete result.strings;
				defered.resolve(result);
			});
		}
		
	    return defered.promise;
	}
	

	
	this.updateSearchOutput = function(id,parameters,type){
		return restfactory.PUT("update-search-output",{searchid:id,output:parameters,type:type});
	};
	
	
	this.updateSearchParamValue = function(searchid,parameter,newValue,type){
		return restfactory.PUT("update-search-param-value",{searchid:searchid,pid:parameter,value:newValue,type:type});
	}
	
	
	this.updateSearchSParameter = function(searchid,pparameter){
		return restfactory.POST("update-search-param",{searchid:searchid,parameter:pparameter,type:'o'});
	}
	
	this.updateSearchPParameter = function(searchid,pparameter){
		return restfactory.POST("update-search-param",{searchid:searchid,parameter:pparameter,type:'p'});
	}
	
	
//	
//	this.goToSearches = function(){
//		console.log("warum bin ich hier?")
//		$state.go("openSearch");
//	}
	
	
	
	this.updateParameterSampleSearch = function(searchid,otparameter){
		return restfactory.POST("update-search-param",{searchid:searchid,otparameter:otparameter,type:1});
	}
	

	
	this.updateSearchName = function(searchID,name,language){
		var promise = restfactory.PUT("update-search-name",{searchID:searchID,newname:name,language:language});
		return promise;
	};
	
	
	
	this.updateSearchType = function(id,searchType){
		var promise = restfactory.PUT("update-search-type",{searchid:id,type:searchType});
		return promise;
	} ;
};


angular.module('unidaplan').service('searchService', ['restfactory','$q','$translate',
               'key2string','languages',searchService]);

})();