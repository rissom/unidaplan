(function(){
  'use strict';

function editSinglePOParameterController($state,$uibModal,$stateParams,$translate,avProcessTypeService,parameter,restfactory,languages){
  

    var thisController = this;
      
    this.compulsory = parameter.compulsory;
    
    this.definition = parameter.definition;
    
    this.format = parameter.format;
        
    this.hidden = parameter.hidden;
    
    this.paramGrpID = parameter.parametergroup;
    
    this.pgnamef = parameter.pgnamef;
    
    this.processtype = parameter.processtype;
        
    this.processtypenamef = parameter.processtypenamef;
          
    this.languages = languages;
  
    this.nL1 = { data    : { value: parameter.nameLang(languages[0].key) },
                 editing : false,
                 field   : "name", 
                 lang    : languages[0].key,
               };

    this.nL2 = { data    : { value: parameter.nameLang(languages[1].key) },
                 editing : false,                                       
                 field   : "name",
                 lang    : languages[1].key
               };

    this.dL1 = { data    : { value: parameter.descLang(languages[0].key) },
                 editing : false,
                 field   : "description",
                 lang    : languages[0].key
               };

    this.dL2 = { data    : { value: parameter.descLang(languages[1].key) },
                 editing : false,
                 field   : "description",
                 lang    : languages[1].key,
               };
    
    this.lang1 = $translate.instant(languages[0].name);
  
    this.lang2 = $translate.instant(languages[1].name);
  
    this.lang1key = languages[0].key;
  
    this.lang2key = languages[1].key;
    
    if (parameter.hasOwnProperty('stringkeyunit')){
        this.unit = true;
	    this.unitL1 = parameter.unitLang(languages[0].key);
	    this.unitL2 = parameter.unitLang(languages[1].key);
    }
  
  
  
  	this.setHidden = function(){
	    var tempParameter={ 
	    		parameterid : parameter.id, 
	    		hidden : thisController.hidden
	    };
	    var promise = avProcessTypeService.updatePOParameter(tempParameter);
	    promise.then(reload,error)
  	};
  
  
  
  	this.setCompulsory = function(){
  		var tempParameter = { parameterid : parameter.id,
                      		  compulsory  : thisController.compulsory
                      		};
  		var promise= avProcessTypeService.updatePOParameter(tempParameter);
        promise.then(reload,error)
  	};

  
  
    this.updateNameOrDescription = function(p) {
  	    	var tParameter = { parameterid : parameter.id };
  	    	if (p.parameter.field === 'description'){
            tParameter.description = {};
            tParameter.description[p.parameter.lang] = p.parameter.data.value;
        } else{
            tParameter.name = {};
            tParameter.name[p.parameter.lang] = p.parameter.data.value;
        }
  	    	var promise = avProcessTypeService.updatePOParameter(tParameter);
  	    	p.editing = false;
  	    	promise.then(reload, error)
    };

  
  
    var reload = function() {
    	    var current = $state.current;
  	  	var params = angular.copy($stateParams);
  	  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };

    
    var error = function() {
        console.error("error");
    };
}

angular.module('unidaplan').controller('editSinglePOParameterController', 
		['$state','$uibModal','$stateParams','$translate','avProcessTypeService','parameter',
		 'restfactory','languages',editSinglePOParameterController]);

})();