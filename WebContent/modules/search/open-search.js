(function(){
'use strict';

function openSearchController(restfactory,$translate,$scope,$state,$stateParams,searches,searchService) {
	
	var thisController=this;

	this.searches=searches;
	
	this.strings = [];
	
	var me ='Thorsten Rissom';
		
	
	
	this.addSearch=function(){
		var name={};
        name[$translate.use()]=$translate.instant("New Search");
		var promise=searchService.addSearch(name);
		promise.then(function(rest){
			$state.go("editSearch",{id:rest.data.id,newSearch:true});
		},
		function(){
			console.log("Error creating new Search");
		});
	};
	
	
	
	this.mySearches = function() {  // returns all my searches
		var mySearches=[];
		angular.forEach(this.searches, function(aSearch) {
			if (aSearch.owner==me) {
				mySearches.push(aSearch);
			}
		});
		return mySearches;
	};
	
	
	  
	this.performAction=function(search,action){
		if (action.action==="edit") {
			$state.go("editSearch",{id:search.id});
		}
		if (action.action=="delete") {
			var promise = searchService.deleteSearch(search);
			promise.then(function(){
				reload();
			},function(){
				console.log("error");
			});
		}
	};
	
	
	
	this.otherSearches = function() {  
		// returns all searches that are not owned by me
		var otherSearches=[];
		angular.forEach(this.searches, function(aSearch) {
			if (aSearch.owner!=me) {
				otherSearches.push(aSearch);
			}
		});
		return otherSearches;
	};
	
	
	
	this.deleteSearch = function(search) {
		var promise=searchService.deleteSearch(search);
		promise.then(function(){
				reload();
			},
			function(){
				console.log("error deleting search");
			});   
	};
	
	
  
	var reload=function() {
		var current = $state.current;
		var params = angular.copy($stateParams);
		return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};
  
	
}
    
        
angular.module('unidaplan').controller('openSearchController',['restfactory','$translate','$scope','$state','$stateParams','searches','searchService',openSearchController]);

})();