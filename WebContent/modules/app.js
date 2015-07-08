(function(){
'use strict';


// Declare app level module which depends on filters, and services
angular.module('unidaplan',['ui.bootstrap','ui.router'])

.config(function($stateProvider, $urlRouterProvider) {
    
    $urlRouterProvider.otherwise('/home');
    
    $stateProvider
        
        // UI-Router STATES AND NESTED VIEWS ========================================
        .state('home', {
            url: '/home',
            templateUrl: 'modules/experiments/experiments.html'
        })
        
        .state('sample', {
        url: '/sample',
        templateUrl: 'modules/sample/sample.html'
        })
        
        .state('process', {
        url: '/process',
        templateUrl: 'modules/process/process.html'
        })
        
        .state('experiments', {
        url: '/experiments',
        templateUrl: 'modules/experiments/experiments.html'
        })
        
        .state('about', {
        url: '/about',
        templateUrl: 'modules/help/about.html'
        })
    
    	.state('help', {
        url: '/help',
        templateUrl: 'modules/help/help.html'
        })
    
    	.state('error', {
    		url: '/error',
    		template: "<h1>ERROR!!!</h1>"
    	})
    })
        
	
.value ('lang',"de")

.config(function() {
	// Put configuration code here
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

    
})
})();


