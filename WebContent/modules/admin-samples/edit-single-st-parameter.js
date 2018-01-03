(function(){
  'use strict';

function editSingleSTParameterController($state,$uibModal,
		$stateParams,$translate,avSampleTypeService,parameter,
		restfactory,sampleService,languages){
  
    var thisController = this;
      
    this.compulsory = parameter.compulsory;
    
    this.format = parameter.format;
    
    this.formula = parameter.formula;
        
    this.hidden = parameter.hidden;
        
    this.paramGrpID = parameter.parametergroup;
    
    this.definition = parameter.definition;
    
    this.min = parameter.min;
    
    this.max = parameter.max;
    
    this.pgnamef = parameter.pgnamef;
    
    this.sampletypenamef = parameter.sampletypenamef;
    
    this.sampletype = parameter.sampletype;
      
    this.languages = languages;
  
    this.NL1 = { data    : { value: parameter.nameLang(languages[0].key) },
                 editing : false,
                 field   : "name", 
                 lang    : languages[0].key,
               };
  
    this.NL2 = { data    : { value: parameter.nameLang(languages[1].key) },
                 editing : false,                                       
                 field   : "name",
                 lang    : languages[1].key
               };
    
    this.DL1 = { data    : { value: parameter.descLang(languages[0].key) },
                 editing : false,
                 field   : "description",
                 lang    : languages[0].key
               };
    
    this.DL2 = { data    : { value: parameter.descLang(languages[1].key) },
                 editing : false,
                 field   : "description",
                 lang    : languages[1].key,
               };
    
    if (parameter.stringkeyunit){
        this.unitL1 = { data    : { value: parameter.unitLang(languages[0].key) },
                editing : false,
                field   : "unit",
                lang    : languages[0].key,
              };
        this.unitL2 = { data    : { value: parameter.unitLang(languages[1].key) },
                editing : false,
                field   : "unit",
                lang    : languages[1].key,
              };
    }
    
    this.lang1 = $translate.instant(languages[0].name);
  
    this.lang2 = $translate.instant(languages[1].name);
  
    this.lang1key = languages[0].key;
  
    this.lang2key = languages[1].key;
    
    this.unit = parameter.stringkeyunit > 0;
    
    this.titlefield = parameter.id_field;
    
    
  
    
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
	  	    		var promise = avSampleTypeService.updateParameter(tParameter);
	  			promise.then(reload,error);
  	    		}
  	    	}, 
  	    	function () {
  	    		// dismissed
  	    	}
  	    );
	};

  	
	this.showFormula = function(){  // decide if formula field is shown.
	    var showF = false; 
	    if (parameter.datatype === "float" || parameter.datatype === "integer"){
	        showF = !this.titlefield;
	    }
		return showF;
	}
    
  
  	this.setHidden = function(){
	    var tParameter = { 
	    		parameterid : parameter.id, 
	    		hidden      : thisController.hidden
	    };
	    var promise = avSampleTypeService.updateParameter(tParameter);
 	    promise.then(reload,error);
  	};
  
  
  
  	this.setCompulsory = function(){
  		var tParameter = { 
  			parameterid : parameter.id,
			compulsory  : thisController.compulsory};
  		var promise = avSampleTypeService.updateParameter(tParameter);
  		promise.then(reload,error);
  	};
  	
  	
  	
  	this.changeField = function(p){
  	    var tParameter = { parameterid : parameter.id }
  	    if (p.parameter.field === "name"){
  	        tParameter.name = {};
  	        tParameter.name[p.parameter.lang] = p.parameter.data.value;
  	    }
        if (p.parameter.field === "description"){
            tParameter.description = {};
            tParameter.description[p.parameter.lang] = p.parameter.data.value;
        }
  	    p.parameter.editing = false;
        var promise = avSampleTypeService.updateParameter(tParameter);
        promise.then(reload,error);
  	}
  	
    
   
    var reload = function() {
    	    var current = $state.current;
  	  	var params = angular.copy($stateParams);
  	  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };

    
    
    var error = function() {
        console.error("error");
    };

}

angular.module('unidaplan').controller('editSingleSTParameterController', 
		['$state','$uibModal','$stateParams','$translate','avSampleTypeService','parameter',
		 'restfactory','sampleService','languages',editSingleSTParameterController]);

})();