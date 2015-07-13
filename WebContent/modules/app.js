(function(){
'use strict';


// Declare app level module which depends on filters, and services
angular.module('unidaplan',['pascalprecht.translate','ui.bootstrap','ui.router'])

.config(function($stateProvider, $urlRouterProvider) {
    
    $urlRouterProvider.otherwise('/open-experiments');
    
    $stateProvider
        
        // UI-Router STATES AND NESTED VIEWS ========================================
        
        .state('sample', {
        url: '/sample/:sampleID',
        templateUrl: 'modules/sample/sample.html',
        controller: "sampleController as ssc"  
        })
        
        
        .state('sChoser', {
        url: '/sample',
        templateUrl: 'modules/sample/sample-choser.html',
        controller: 'sampleChoser as sampleChoserCtrl'
        })
        
        .state('process', {
        url: '/process',
        templateUrl: 'modules/process/process.html'
        })
        
        .state('openExperiment', {
        url: '/open-experiments',
        templateUrl: 'modules/experiments/open-experiment.html'
        })
        
        .state('experiment', {
    	url: '/experiment/:experimentID',
        templateUrl: 'modules/experiments/experiment.html',
        controller: function($scope, $stateParams) {
             // get the id
             $scope.id = $stateParams.experimentID; }
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
    'Files': 'Dateien',
    'Sample Tree': 'Probenbaum',
    'Planned Processes': 'Planung',
    'delete sample': 'Probe löschen',
    'Experiment': 'Experiment',
    'Help': 'Hilfe',
    'Sample': 'Probe',
    'Samples': 'Proben',
    'Process': 'Prozess',
    'next': 'nächste',
  	'previous': 'vorherige',
  	'Is ancestor of': 'Vorfahr von',
  	'Originates from': 'Stammt ab von',
  	'Delete Sample': 'Probe löschen',
  	'New Process' : 'Neuer Prozess',  	
  	'Help page': 'Hilfe Seite',
  	'New Sample': 'Neue Probe',
  	'Recent Samples' : 'zuvor geöffnete Proben',
  	'Recent Processes' : 'Zuvor geöffnete Prozesse',
  	'Show Process' : 'Zeige Prozess',
  	'Show Sample' : 'Zeige Probe',
  	'Show Samples' : 'Zeige Proben',
  	'New Experiment' : 'Neues Experiment',
  	'Open Experiments' : 'Experimente öffnen',
  	'Groups and Users' : 'Benutzer und Gruppen',
  	'Parameters' : 'Parameter',
  	'Experiments' : 'Experimente',
  	'Samples in process' : 'Proben im Prozess',
  	'Add/Remove samples' : 'Proben hinzufügen/entfernen',
  	'Delete Process' : 'Prozess löschen',
  	'Status of process run' : 'Verlauf des Prozesses',
  	'Processes' : 'Prozesse',
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
    'Available Samples':'Verfügbare Proben'
  });
 
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


