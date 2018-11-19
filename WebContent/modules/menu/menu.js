(function(){
'use strict';

function menuf(experimentService,searchService,restfactory,$translate,$transitions,
			   $rootScope,$state,userService) {

	var thisController = this;
		
	this.navCollapsed = true;
	
	
	
	this.language = function(){
		return $translate.use();
	};
	
	
	
	this.addSearch = function(){
		var name = {}
        name[this.language()] = $translate.instant("New Search");
		var promise=searchService.addSearch(name);
		promise.then(function(rest){
			$state.go("editSearch",{id:rest.data.id, newSearch:true})
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
		if (this.old_language != $translate.use()) {
			$translate.use(lang);
			if ($rootScope.userid){
				userService.setLanguage($rootScope.userid,lang);
			}
		}
	};
	
	

	$transitions.onBefore({},function(trans) {
		thisController.navCollapsed = true;
		
      // if user is not logged in: track the state the user wants to go to
		var toState = trans.targetState();
		var toStateName = toState.name();
		if (toStateName != "login" && toStateName != "noRights" && toStateName != "signup"){
			if ($rootScope.userid == undefined || $rootScope.userid < 1){
				$rootScope.failedState = toState; // save desired state
//				console.log("userid=undefined")
//				return trans.router.stateService.target('login');
			} 
		}
    });
	
	
	
	$transitions.onError({},
		function(trans) {
        	    console.log("Hello, error");
        	    
        		
        		// Output error message and detail, if they exist.
        		var error = trans.error();
        		console.log("error",error);
        		if (error.detail){
        			console.log("yes detail")
        			if (error.detail === "401") {
        				// save the desired state
        				var toState = trans.targetState();
        				var toStateName = toState.name();
                        console.log ("toStateName",toStateName)
        
        				if (toStateName != "login" && toStateName != "noRights" && toStateName != "signup"){
        					$rootScope.failedState = toState;
        				}
        				$state.go('noRights');
        			}
        			
        			if (error.detail === "404") {
        				alert ("Not Found!");
        				$state.go('sampleChoser');
        			}
        			
        			if (error.detail === "511") {
        				// save the desired state
        				var toState = trans.targetState();
        				var toStateName = toState.name();
                    console.log ("trans",trans)
                    console.log ("toState",toState)
                    console.log ("toStateName",toStateName)
        				if (toStateName != "login" && toStateName != "noRights" && toStateName != "signup"){
        					$rootScope.failedState = toState;
        				}
        				delete $rootScope.username;
        				delete $rootScope.userid;
        				delete $rootScope.userfullname;
        				$rootScope.admin = false
        				$state.go('noRights');
        			}
        		}	
        }
	);
	 
	
	
	
	$transitions.onSuccess({}, function(trans) {
	        // track the state the user wants to go to; authorization service needs this
	    if ($state.name){
          	if ($state.name!="login" && $state.name!="noRights"){
          	    console.log("$state.name",$state.name)
    			delete $rootScope.failedState;
      	    }
	    }
    });
	
	
	
	this.logout = function(){
		var promise = restfactory.GET('logout');
		promise.then(function(){
				delete $rootScope.username;
				delete $rootScope.failedState;
				$rootScope.admin = false;
				delete $rootScope.userid;
				$state.go('login');
			},
			function(){
				console.log("Error logging out!");	
			});
	};
	
	
}

angular.module('unidaplan').controller('menu',['experimentService',
    'searchService','restfactory','$translate','$transitions','$rootScope','$state','userService',menuf]);
  
})();