(function(){
'use strict';


// Declare app level module which depends on filters, and services
angular.module('unidaplan',['pascalprecht.translate','ui.bootstrap','ui.router'])

.constant("languages",[{"key": "de","name":"german"},{"key": "en","name":"english"}])

.config(function($stateProvider, $urlRouterProvider) {
    
	
	$urlRouterProvider.otherwise('/login');
	
	
    $stateProvider
        
        // UI-Router STATES AND NESTED VIEWS
               
        
        .state('about', {
	        url: '/about',
	        templateUrl: 'modules/help/about.html'
        })
        
                
        .state('adminProcesses', {
	        url: '/adminprocesses',
	        templateUrl: 'modules/admin-processes/admin-processes.html',
	        controller:"aProcessesController as aProcessesCtrl",
	        resolve:{
               	ptypes: function(avProcessTypeService){
        	   	    	return avProcessTypeService.getProcessTypes()
        	   	    }
			}
        })
        
        
        .state('adminSamples', {
	        url: '/adminsamples',
	        templateUrl: 'modules/admin-samples/admin-samples.html',
	        controller:"aSamplesController as aSamplesCtrl",
	        resolve:{
               	types: function(avSampleTypeService){
        	   	    	return avSampleTypeService.getSampleTypes()
        	   	    }
			}
        })
        
        
        .state('choseProcess', {
	        url: '/chose-process',
	        templateUrl: 'modules/process/chose-process.html',
	        controller:'choseProcessController as choseProcessCtrl',
	        resolve:{
                ptypes: 
                	function(avProcessTypeService){
        	   	    	return avProcessTypeService.getProcessTypes()
        	   	    }
	        }
        })
        
        
        .state('editPtParamGrps', {
        	url: '/editprocesstype/{processTypeID:int}',
	        templateUrl: 'modules/admin-processes/edit-pt-param-grps.html',
	        controller:"editPtParamGrpsController as editPtParamGrpsCtrl",
	        resolve:{
	        	processType: function(avProcessTypeService,$stateParams){
        	   	    	return avProcessTypeService.getPTypeParamGrps($stateParams.processTypeID);
        	   	    }
			}
        })
        
        
        .state('editPtParams', {
        	url: '/editprocesstypeparams/{paramGrpID:int}',
	        templateUrl: 'modules/admin-processes/edit-pt-params.html',
	        controller:"editPtParamsController as editPtParamsCtrl",
	        resolve:{
	        	parameterGrp: function(avProcessTypeService,$stateParams){
        	   	    	return avProcessTypeService.getPTypeParams($stateParams.paramGrpID);
        	   	    },
		        avParameters: 
		        	function(parameterService){
			   	    	return parameterService.getParameters()
			   	    }
				}
        })
        
        
        .state('editSTParamGrps', {
        	url: '/editsampletype/{sampleTypeID:int}',
	        templateUrl: 'modules/admin-samples/edit-sample-param-grps.html',
	        controller:"editSampleParamGrpsController as editSampleParamGrpsCtrl",
	        resolve:{
	        	sampleType: function(avSampleTypeService,$stateParams){
        	   	    	return avSampleTypeService.getSampleTypeParamGrps($stateParams.sampleTypeID);
        	   	    }
			}
        })
        
        
        .state('error', {
    		url: '/error',
    		template: "<h1>ERROR!!!</h1>"
    	})
        
    	
        .state('login', {
	    	url: '/login',
	    	templateUrl: 'modules/login/login.html',
	    	controller: 'loginController as loginCtrl'
    	})
    	
    	
        .state('openExperiment', {
	        url: '/experiments',
	        controller:'oExpController as oexpCtrl',
	        templateUrl: "modules/experiments/open-experiment.html",
	        resolve: {
	        	experiments:
	        		function(experimentService){
	        			return experimentService.getExperiments();
	        		}
	        }
        })
        
        
        .state('openSearch', {
	        url: '/searches',
	        controller:'oSearchController as oSearchCtrl',
	        templateUrl: "modules/experiments/open-searches.html",
	        resolve: {
	        	searches:
	        		function(searchService){
	        			return searchService.getSearches();
	        		}
	        }
        })

        
        .state('editSTParams', {
        	url: '/editsampletypeparams/{paramGrpID:int}',
	        templateUrl: 'modules/admin-samples/edit-st-params.html',
	        controller:"editSTParamsController as editSTParamsCtrl",
	        resolve:{
	        	parameterGrp: function(avSampleTypeService,$stateParams){
        	   	    	return avSampleTypeService.getSTypeParams($stateParams.paramGrpID);
        	   	    },
		        avParameters: 
		        	function(parameterService){
			   	    	return parameterService.getParameters()
			   	    }
				}
        })
        
        
        .state('editSearch', {
	    	url: '/edit-search',
	        templateUrl: 'modules/search/edit-search.html',
	        controller: 'editSearchController as editSearchCtrl',
	        resolve:{
                sampleTypes: 
            	    function(avSampleTypeService){
        	   	    	return avSampleTypeService.getSampleTypes()
                	},
                ptypes: 
                	function(avProcessTypeService){
        	   	    	return avProcessTypeService.getProcessTypes()
        	   	    },
        	   	avParameters: 
        	   		function(parameterService){
		   	    		return parameterService.getParameters()
        	   		}
	        }
        })
        
        
        .state('experiment', {
	    	url: '/experiment?:experimentID&:editmode',
	        templateUrl: 'modules/experiments/experiment.html',
	        controller: 'experimentController as experimentCtrl',
	        resolve:{
	            stypes: 
	        	    function(avSampleTypeService){
	        	   	    return avSampleTypeService.getSampleTypes()
	                },
                ptypes: 
                	function(avProcessTypeService){
        	   	    	return avProcessTypeService.getProcessTypes()
        	   	    },
	            experimentData: 
	            	function(experimentService,$stateParams){
	        			return experimentService.getExperiment($stateParams.experimentID)
	        	    },
		        avParameters: 
		        	function(parameterService){
			   	    	return parameterService.getParameters()
			   	    }
	        }
        })
           
        .state('groups', {
	    	url: '/admin/groups',
	        templateUrl: 'modules/groups/groups.html',
	        controller: 'groupController as groupCtrl'
        })
        
            
        .state('help', {
	        url: '/help',
	        templateUrl: 'modules/help/help.html'
        })
        
        
        .state('newSample', {
	    	url: '/new-sample',
	        templateUrl: 'modules/sample/new-sample.html',
	        controller: 'newSampleController as newSampleCtrl',
	        resolve:{
                types: 
            	    function(avSampleTypeService){
        	   	    	return avSampleTypeService.getSampleTypes()
                	}
	        }
        })
    
            
        .state('newProcess', {
	    	url: '/new-process',
	        templateUrl: 'modules/process/new-process.html',
	        controller: 'newProcessController as newProcessCtrl',
	        resolve:{
                ptypes: 
                	function(avProcessTypeService){
        	   	    	return avProcessTypeService.getProcessTypes()
        	   	    }
	        }
        })
        
        
        .state('parameter', {
	        url: '/parameter',
	        templateUrl: 'modules/parameters/parameters.html',
	        controller:'parameterController as parameterCtrl',
	        resolve:{
                parameters: 
                	function(parameterService){
        	   	    	return parameterService.getParameters()
        	   	    }
	        }
        })
        
        
        .state('process', {
	        url: '/process/{processID:int}',
	        templateUrl: 'modules/process/process.html',
	        controller:"process as processCtrl",
	        resolve:{
	            types: 
	        	    function(avSampleTypeService){
	        	   	    return avSampleTypeService.getSampleTypes()
	                },
	            processData: 
	            	function(processService,$stateParams){
	        			return processService.getProcess($stateParams.processID)
	        	    },
               	ptypes: function(avProcessTypeService){
        	   	    	return avProcessTypeService.getProcessTypes()
        	   	    }
			}
        })
        
        
        .state('recentSamples', {
			url: '/recentsamples',
			templateUrl: 'modules/sample/recent-samples.html',
		    controller: 'recentSampleController as recentSampleCtrl',
		    resolve:{
	                types: 
	            	    function(avSampleTypeService){
	        	   	    	return avSampleTypeService.getSampleTypes()
	                	}
		    }
    	})
        
        
        .state('recentProcesses', {
			url: '/recent-processes',
			templateUrl: 'modules/process/recent-processes.html',
		    controller: 'recentProcessController as recentProcessCtrl',
		    resolve:{
	                ptypes: 
	            	    function(avProcessTypeService){
	        	   	    	return avProcessTypeService.getProcessTypes()
	                	}
		    }
    	})
    	
        
        .state('recentExperiments', {
			url: '/recent-experiments',
			templateUrl: 'modules/experiments/recent-experiments.html',
		    controller: 'recentExperimentsController as recentExperimentsCtrl'
    	})
    	
    	
    	.state('sampleChoser', {
	        url: '/sample',
	        templateUrl: 'modules/sample/sample-choser.html',
	        controller: 'sampleChoser as sampleChoserCtrl',
	        resolve: {
	            types: 
                    function(avSampleTypeService){
                		return avSampleTypeService.getSampleTypes()
                	}
	        }
        })
        
        
        .state('sample', {
	        url: '/sample/{sampleID:int}',
	        templateUrl: 'modules/sample/sample.html',
	        controller: "sampleController as sampleCtrl",
	        resolve: {
	        	sample: function($stateParams,sampleService){
	        				return sampleService.loadSample($stateParams.sampleID)
	        			},
			    types:  function(avSampleTypeService){
        	   	    		return avSampleTypeService.getSampleTypes()
        	   	    	},
        	   	ptypes: function(avProcessTypeService){
            	   	    	return avProcessTypeService.getProcessTypes()
            	   	    }
	        }
        })
            	
        
        .state('signup', {
	    	url: '/signup/{userID:int}/{token:string}',
	    	templateUrl: 'modules/signup/signup.html',
	    	controller: 'signupController as signupCtrl',
	    	resolve:{
	            user: 
	        	    function(userService,$stateParams){
	        	   	    return userService.getUser($stateParams.userID,$stateParams.token);
	            	},
	        	token: 
	        	   	function($stateParams){
	        			return $stateParams.token;
	            	}
	    	}
    	})
    	
        
        .state('users', {
	    	url: '/admin/users',
	        templateUrl: 'modules/users/users.html',
	        controller: 'userController as userCtrl',
	        resolve:{
	            users: 
	        	    function(userService){
	        	   	    return userService.getUsers()
	                }
			 }
        })
    
    })
       
    
.config(['$translateProvider', function ($translateProvider) {
  $translateProvider.useSanitizeValueStrategy('escape');
  $translateProvider.useStaticFilesLoader({
      prefix: 'languages/lang-',
      suffix: '.json'
   });
  $translateProvider.preferredLanguage('de');
}])

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


