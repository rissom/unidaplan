(function(){
'use strict';

var searchService = function (restfactory,$q,$translate,key2string,languages) {
	// restfactory is a wrapper for $html.

	var thisController=this;	
	
	this.addSearch = function(name) {
		return restfactory.POST("add-search",{"name":name});
	}
	
    
	
	this.getSearches = function(experiment){
		return 0
	}
	
	
	
	this.getSearch = function(searchID){
		var defered=$q.defer();
		var promise = restfactory.GET("search?id="+searchID);
		promise.then(function(rest) {
		  	thisController.search = rest.data.search;
		  	thisController.strings = rest.data.strings;
	  		thisController.search.namef=function(){
	  			return key2string.key2string(thisController.search.name,thisController.strings);
	  		}
	  		thisController.search.nameL1=
	  			key2string.key2stringWithLangStrict(thisController.search.name,
	  			thisController.strings,languages[0].key);
		  	thisController.search.nameL2=
		  		key2string.key2stringWithLangStrict(thisController.search.name,
		  		thisController.strings,languages[1].key);	  		
			defered.resolve(thisController.search);
		});
       return defered.promise
	}
	
	
	
	this.updateSearchName = function(searchID,name,language){
		var promise = restfactory.PUT("update-search-name",{searchID:searchID,newname:name,language:language})
		return promise;
	}
	
	
	
	// delete an experiment (also from recent experiments)
	this.deleteSearch = function(searchID){
		return restfactory.DELETE("delete-search?searchid="+searchID);
	}
	
	
	
	
}


angular.module('unidaplan').service('searchService', ['restfactory','$q','$translate',
               'key2string','languages',searchService]);

})();