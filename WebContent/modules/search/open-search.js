(function(){
'use strict';

function openSearchController(restfactory,$translate,$rootScope,$scope,$state,$stateParams,searches,searchService) {
	
	var thisController = this;

	this.searches = searches;
	
	this.strings = [];
		
	
	// contains all searches of the current user
	this.mySearches = [];
	// contains all the searches that are not owned by me
	this.otherSearches = [];
	
	angular.forEach(this.searches, function(aSearch) {
		if (aSearch.ownerid == $rootScope.userid) {
			thisController.mySearches.push(aSearch);
		}else{
			thisController.otherSearches.push(aSearch);
		}
	});
	

	
	this.addSearch = function(){
		var name = {};
        name[$translate.use()]=$translate.instant("New Search");
		var promise = searchService.addSearch(name);
		promise.then(function(rest){
			$state.go("editSearch",{id:rest.data.id,newSearch:true});
		},
		function(){
			console.log("Error creating new Search");
		});
	};
	
	
	
	  
	this.performAction = function(search,action){
		if ( action.action === "edit" ) {
			$state.go("editSearch",{id:search.id});
		}
		if ( action.action == "delete" ) {
			var promise = searchService.deleteSearch(search);
			promise.then(reload,
			    function(){
				    console.log("error");
			    }
			);
		}
	};
	
	
	
	
	this.deleteSearch = function(search) {
		var promise = searchService.deleteSearch(search);
		promise.then(reload,
			function(){
				console.log("error deleting search");
			}
		);   
	};
	
	
  
	var reload = function() {
		var current = $state.current;
		var params = angular.copy($stateParams);
		return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};
  
	
}
    
        
angular.module('unidaplan').controller('openSearchController',['restfactory','$translate','$rootScope','$scope','$state','$stateParams','searches','searchService',openSearchController]);

})();