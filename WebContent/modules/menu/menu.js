(function(){
'use strict';

function menuf(experimentService,searchService,restfactory,$translate,
						$rootScope,$state,userService) {

	var thisController = this;
		
	this.navCollapsed = true;
	
	
	
	
	
	
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
	
	
	
	this.newExperiment = function(){
		var promise = experimentService.addExperiment();
		promise.then(function(rest){ 
				console.log("rest",rest)
				$state.go("experiment",{"experimentID":rest.data.id, "editmode" : "true"})
			},function(){
				console.log("error");
			}
		)
	};
	
	
	
	this.setLanguage = function(lang){  // change language and send a broadcast
		if (this.old_language!=$translate.use()) {
			$translate.use(lang);
			window.localStorage.setItem("language",lang);
			if ($rootScope.userid){
				userService.setLanguage($rootScope.userid,lang);
			}
		}
	};
	
	
	
	$rootScope.$on('$stateChangeStart', 
			function(event, toState, toParams, fromState, fromParams){ 
				thisController.navCollapsed = true;
	});
	
	
	
	this.logout = function(){
		var promise = restfactory.GET('logout');
		promise.then(function(){
				delete $rootScope.username;
				window.localStorage.removeItem("username");
				$rootScope.admin = false;
		    	window.localStorage.removeItem("admin");
				delete $rootScope.userid;
		    	window.localStorage.removeItem("userid");
				$state.go('login');
			},
			function(){
				console.log("Error!");	
			});
	};
	
	
}

angular.module('unidaplan').controller('menu',['experimentService',
    'searchService','restfactory','$translate','$rootScope','$state','userService',menuf]);
  
})();