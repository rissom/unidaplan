(function(){
'use strict';


// Declare app level module which depends on filters, and services
angular.module('unidaplan',['ui.bootstrap'])
	
.config(function() {
	// Put configuration code here
})


.directive('samplebutton', function(){
	return {
		restrict: 'E',
//		scope: {buttonsample : '='},
		template: '<button class="btn btn-default" ng-click="menuCTRL.select(\'sample\'); loadData(buttonsample.id)"> '
		+'{{buttonsample.type}} {{buttonsample.name}} '
		+'</button>',
	};
})



.run(function($rootScope, restfactory) {
	/** ============================================== PRINT DEBUG ========================================================== 
	 * debug > 3 - print all 
	 * debug > 2 - print debug  
	 * debug > 1 - print error and Warnings 
	 * debug > 0 - print error 
	 * debug = 0 - print nothing 
	 */
	
	$rootScope.DEBUG_NONE  		= 0; 
	$rootScope.DEBUG_ERROR  	= 1; 
	$rootScope.DEBUG_WARNING  	= 2; 
	$rootScope.DEBUG_DEBUG  	= 3; 
	$rootScope.DEBUG_INFO  		= 4; 
	$rootScope.DEBUG_ALL  		= 5; 
	
	$rootScope.debugLevel = $rootScope.DEBUG_ALL; 
	
	/** print debug*/ 
    $rootScope.log_D = function (text, obj) {
    	if ($rootScope.debugLevel >= $rootScope.DEBUG_DEBUG) {
			if (arguments.length >1) {
				console.log(" DEBUG: "+text,obj); 
//				console.log("caller is " + arguments.callee.caller.toString()+" text"+text,obj); 
			} else {
//				console.log("caller is " + arguments.callee.caller.toString());
				console.log(" DEBUG: "+text);
			}
    	}
    };
    
    /** print errors */ 
    $rootScope.log_E = function (text, obj) {
    	if ($rootScope.debugLevel >= $rootScope.DEBUG_ERROR) {
			if (arguments.length >1) {
				console.log(" ERROR: "+text,obj); 
			} else {
				console.log(" ERROR: " + text);
			}
			console.trace();
    	}
    };
    
    /** print warnings */
    $rootScope.log_W = function (text, obj) {
    	if ($rootScope.debugLevel >= $rootScope.DEBUG_WARNING) {
			if (arguments.length >1) {
				console.log(" WARNING: "+text,obj); 
			} else {
				console.log(" WARNING: "+text);
			}
    	}
    };
    /** print Info */
    $rootScope.log_I = function (text, obj) {
    	if ($rootScope.debugLevel >= $rootScope.DEBUG_INFO) {
    		if (arguments.length >1) {
    			console.log(" INFO: "+text,obj); 
    		} else {
    			console.log(" INFO: "+text);
    		}
    	}
    };
  
    /*
     * 
     * 
     * 
     * 
     * 
     */
        

    
})
})();


