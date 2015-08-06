(function(){
'use strict';


// Declare app level module which depends on filters, and services
angular.module('unidaplan',['pascalprecht.translate','ui.bootstrap','ui.router'])

.config(function($stateProvider, $urlRouterProvider) {
    
    $urlRouterProvider.otherwise('/login');
    
    $stateProvider
        
        // UI-Router STATES AND NESTED VIEWS
        
        .state('sample', {
	        url: '/sample/{sampleID:int}',
	        templateUrl: 'modules/sample/sample.html',
	        controller: "sampleController as sampleCtrl",
	        resolve: {
	        	sample: function($stateParams,sampleService){
	        				return sampleService.loadSample($stateParams.sampleID)
	        			},
			    types:  function(avSampleTypeService){
        	   	    		return avSampleTypeService.getTypes()
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
    	
    	.state('newSample', {
	    	url: '/new-sample',
	        templateUrl: 'modules/sample/new-sample.html',
	        controller: 'newSampleController as newSampleCtrl',
	        resolve:{
                types: 
            	    function(avSampleTypeService){
        	   	    	return avSampleTypeService.getTypes()
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
        
        .state('sampleChoser', {
	        url: '/sample',
	        templateUrl: 'modules/sample/sample-choser.html',
	        controller: 'sampleChoser as sampleChoserCtrl',
	        resolve: {
	            types: 
                    function(avSampleTypeService){
                		return avSampleTypeService.getTypes()
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
	        	   	    return avSampleTypeService.getTypes()
	                },
	            processData: 
	            	function(processService,$stateParams){
	        			return processService.getProcess($stateParams.processID)
	        	    }
			}
        })
        
        .state('openExperiment', {
	        url: '/experiments',
	        controller:'oExpController as oexpCtrl',
	        templateUrl: "modules/experiments/open-experiment.html"
        })
        
        .state('experiment', {
	    	url: '/experiment/{experimentID:int}',
	        templateUrl: 'modules/experiments/experiment.html',
	        controller: 'expController as expCtrl'
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
        
        .state('groups', {
	    	url: '/admin/groups',
	        templateUrl: 'modules/groups/groups.html',
	        controller: 'groupController as groupCtrl'
        })
        
        .state('login', {
	    	url: '/login',
	    	templateUrl: 'modules/login/login.html',
	    	controller: 'loginController as loginCtrl'
    	})
    	
        .state('about', {
	        url: '/about',
	        templateUrl: 'modules/help/about.html'
        })
        
    
    	.state('help', {
	        url: '/help',
	        templateUrl: 'modules/help/help.html'
        })
        
        .state('recentsamples', {
			url: '/recentsamples',
			templateUrl: 'modules/sample/recent-samples.html',
		    controller: 'recentSampleController as recentSampleCtrl',
		    resolve:{
	                types: 
	            	    function(avSampleTypeService){
	        	   	    	return avSampleTypeService.getTypes()
	                	}
		    }
    	})
    
    	.state('error', {
    		url: '/error',
    		template: "<h1>ERROR!!!</h1>"
    	})
    })
       
    
.config(['$translateProvider', function ($translateProvider) {
  $translateProvider.useSanitizeValueStrategy('escape');
  $translateProvider.translations('en', {
  	'Params' : 'Parameters',
  	'abouttext' : 'Software to organize your experiments',
	'HELPTEXT' : 'Unidaplan is web-based software for the administration of scientific samples.'			
  });
 
  $translateProvider.translations('de', {
	'About Unidaplan': 'Über Unidaplan',	
    'Add File': 'Datei hinzufügen',
    'administration': 'Administration',	
    'My Experiments': 'Meine Experimente',
    'Other Experiments': 'Andere Experimente',
    'Base Parameters': 'Basisparameter',
    'Cancel' : 'Abbrechen',
    'chosen samples' : 'ausgewählte Proben',
    'Create Process' : 'Prozess erstellen',
    'Divide sample in several new samples' : 'Probe in neue Proben aufteilen',
    'Assign to process' : 'Mit Prozess assozieren',
    'created by' : 'erstellt von',
    'Create Sample' : 'Probe erstellen',
    'Enter your e-mail here' : 'Bitte E-Mail-Adresse hier eingeben',
    'Enter full name here' : 'Bitte vollen Namen hier eingeben',
    'Files': 'Dateien',
    'Finished Processes' : 'Durchgeführte Prozesse',
    'Go to process' : 'Zum Prozess gehen',
    'Mon' : 'Mo',
    'Tue' : 'Di',
    'Wed' : 'Mi',
    'Thu' : 'Do',
    'Fri' : 'Fr',
    'Sat' : 'Sa',
    'Sun' : 'So',
    'Sample Tree': 'Probenbaum',
    'Planned Processes': 'Planung',
    'delete sample': 'Probe löschen',
    'Experiment': 'Experiment',
    'No.' : 'Nr.',
    'Help' : 'Hilfe',
    'Sample' : 'Probe',
    'Samples' : 'Proben',
    'Process' : 'Prozess',
    'next': 'nächste',
  	'previous' : 'vorherige',
  	'Is ancestor of' : 'Vorfahr von',
  	'Originates from' : 'Stammt ab von',
  	'Delete Sample' : 'Probe löschen',
  	'Delete Experiment' : 'Experiment löschen',
  	'Enter your password here' : 'Bitte Passwort hier eingeben',
  	'Enter your username here' : 'Bitte Benutzernamen hier eingeben',
  	'New Process' : 'Neuer Prozess',
  	'no.' : 'Nr.',  
	'day' : 'Tag',
	'days' : 'Tage',
	'hour' : 'Stunde',
	'hours' : 'Stunden',
  	'Help page' : 'Hilfe Seite',
  	'Log in' : 'Anmeldung',
  	'Login' : 'Anmelden',
  	'New Sample': 'Neue Probe',
  	'Select processtype' : 'Prozesstyp wählen',
  	'Password' : 'Passwort',
  	'Re-enter Password' : 'Passwort wiederholen',
  	'Re-enter your password here' : 'Bitte Passwort hier wiederholen',
  	'Recent Samples' : 'zuvor geöffnete Proben',
  	'Recent Processes' : 'Zuvor geöffnete Prozesse',
  	'Select sampletype' : 'Wähle Probentyp',
  	'set role' : 'Rolle setzen',
  	'Show Process' : 'Zeige Prozess',
  	'Show Sample' : 'Zeige Probe',
  	'Show Samples' : 'Zeige Proben',
  	'Sign Up' : 'Registrieren',
  	'New Experiment' : 'Neues Experiment',
  	'Open Experiments' : 'Experimente öffnen',
  	'Groups and Users' : 'Benutzer und Gruppen',
  	'Parameters' : 'Parameter',
  	'Experiments' : 'Experimente',
  	'Recently viewed Samples' : 'Zuletzt angesehene Proben',
  	'Samples in process' : 'Proben im Prozess',
  	'Add/Remove samples' : 'Proben hinzufügen/entfernen',
  	'Delete Process' : 'Prozess löschen',
  	'Status of process run' : 'Verlauf des Prozesses',
  	'Processes' : 'Prozesse',
  	'Logout' : 'Abmelden',
  	'Params' : 'Parameter',
  	'and' : 'und',
  	'Made with' : 'programmiert mit',
  	'abouttext' : 'Software für die Verwaltung wissenschaftlicher Proben',
  	'see license' : 'Lizenz',
  	'as well as' : 'So wie',
  	'HELPTEXT' : 'Unidaplan ist eine Webbasierte Software um wissenschaftliche Proben zu verwalten. Es können Probentypen'+
  		  '(z.B. Pulverproben, Solarzellen, Halbleiterlaser, usw.) definiert werden. Die Proben können bestimmte'+
  		  'Parameter und Eigenschaften aufweisen. Die Auswahl und Definition der Parameter erfolgt über den'+
  		  'Adminstrator.',
    'Available Samples':'Verfügbare Proben',
    'Users' : 'Benutzer',
    'Username' : 'Benutzername',
    'Delete User' : 'Lösche Benutzer',
    'role' : 'Rolle',
    'E-mail' : 'E-Mail',
    'Groups' : 'Gruppen',
    'Delete Group' : 'Gruppe löschen',
    'Members' : 'Mitglieder',
    'Search for Sample' : 'Suche nach Probe',
    'User' : 'Benutzer',
    'last Login' : 'letzes Mal eingeloggt',
    'Enter samplename here' : 'Probennamen hier eingeben',
    'Available sample types' : 'Mögliche Probentypen',
    'Add User' : 'Benutzer hinzufügen'});
 
  $translateProvider.preferredLanguage('en');
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


