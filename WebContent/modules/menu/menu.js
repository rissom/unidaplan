(function(){
'use strict';

function menuf(searchService,restfactory,$translate,$rootScope,$state) {

	var thisController=this;
		
	this.navCollapsed=true;
	
	
	
	
	
	
	this.language = function(){
		return $translate.use();
	};
	
	
	
	this.addSearch=function(){
		var name={}
        name[this.language()]=$translate.instant("New Search");
		var promise=searchService.addSearch(name);
		promise.then(function(rest){
			$state.go("editSearch",{id:rest.data.id,newSearch:true})
		},
		function(){
			console.log("Error creating new Search");
		});
	};
	
	
	
	this.setLanguage = function(lang){  // change language and send a broadcast
		if (this.old_language!=$translate.use()) {
			$translate.use(lang);
			if (thisController.checkLocalStorageSupport()) {
				window.localStorage.setItem("language",lang);
			};
		}
	};
	
	
	
	$rootScope.$on('$stateChangeStart', 
			function(event, toState, toParams, fromState, fromParams){ 
				thisController.navCollapsed = true;
	});
	
	
	
	this.logout = function(){
		var promise = restfactory.GET('logout');
		promise.then(function(){
				$rootScope.username=$translate.instant("User");
				window.localStorage.removeItem("username");
				$rootScope.admin=false;
		    	window.localStorage.setItem("admin",false);
				$rootScope.userid=0;
			    window.localStorage.setItem("userid",0);
				$state.go('login');
			},
			function(){
				console.log("Error!");	
			});
	};
	
	
}

angular.module('unidaplan').controller('menu',['searchService','restfactory','$translate','$rootScope','$state',menuf]);
  
})();