(function(){
  'use strict';

function editSinglePTParameterController($state,$uibModal,$stateParams,$translate,avProcessTypeService,parameter,restfactory,languages){
  

    var thisController = this;
    
    this.compulsory = parameter.compulsory;
    
    this.definition = parameter.definition;
    
    this.format = parameter.format;
    
    this.formula = parameter.formula;
        
    this.hidden = parameter.hidden;
    
    this.paramGrpID = parameter.parametergroup;
    
    this.pgnamef = parameter.pgnamef;
    
    this.processtype = parameter.processtype;
        
    this.processtypenamef = parameter.processtypenamef;
          
    this.languages = languages;
    
    this.lang1 = $translate.instant(languages[0].name);
  
    this.lang2 = $translate.instant(languages[1].name);
  
    this.lang1key = languages[0].key;
  
    this.lang2key = languages[1].key;
    
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
    
    if (parameter.stringkeyunit){
	    this.unitL1 = parameter.unitLang(languages[0].key);
	    this.unitL2 = parameter.unitLang(languages[1].key);
    }
    
    this.unit = parameter.stringkeyunit > 0;
        
    
    
    this.openFormulaModal = function(){
  		var modalInstance = $uibModal.open({
  			animation: false,
  			templateUrl: 'modules/admin-samples/formula-modal.html',
  			controller: 'formulaModalController as formulaModalCtrl',
  			resolve: {
  				formula : function(){
  					return parameter.formula;
  				},
  				paramGroups: function(){
  					return parameter.parametergroups;
  				},
  				parameters: function () {
  					return parameter.otherparameters; 
  				}
  			}
  	    });

  	    modalInstance.result.then(
      	    	function (formula) {
      	    	    if (formula != undefined && formula.ok){
        	  	  	    	var tParameter = {parameterid : parameter.id};
        	  	    		tParameter.formula = formula.formula;
        	  	    		thisController.formula = formula.formula;
        	  	    		var promise = avProcessTypeService.updateParameter(tParameter);
        	  			promise.then(reload,error);
      	    		}
      	    	}, 
      	    	function () {} 	// dismissed
      	);
	};

  
  
  
  
  	this.setHidden = function(){
	    var tempParameter = { 
	    		parameterid : parameter.id, 
	    		hidden : thisController.hidden
	    };
	    var promise = avProcessTypeService.updateParameter(tempParameter);
 	    promise.then(reload,error);
  	};
  
  
  
  	this.setCompulsory = function(){
  		var tempParameter = { 
  			parameterid : parameter.id,
			compulsory  : thisController.compulsory};
  		var promise= avProcessTypeService.updateParameter(tempParameter);
  		promise.then(reload,error);
  	};
  	
  
  
  
    this.update = function(p) {
  	    	var tParameter = {parameterid:parameter.id};
  	    	if (p.parameter.field === 'description'){
  	    		tParameter.description = {};
  	    		tParameter.description[p.parameter.lang] = p.parameter.data.value;
  	    	} else{
  	    		tParameter.name = {};
  	    		tParameter.name[p.parameter.lang] = p.parameter.data.value;
  	    	}
	  	var promise = avProcessTypeService.updateParameter(tParameter);
	  	p.editing = false
	    promise.then(reload, error);
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

angular.module('unidaplan').controller('editSinglePTParameterController', 
		['$state','$uibModal','$stateParams','$translate','avProcessTypeService','parameter',
		 'restfactory','languages',editSinglePTParameterController]);

})();