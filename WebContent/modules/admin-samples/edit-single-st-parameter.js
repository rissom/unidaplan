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
  
    this.nameL1 = parameter.nameLang(languages[0].key);
  
    this.newNameL1 = this.nameL1;
  
    this.nameL2 = parameter.nameLang(languages[1].key);

    this.newNameL2 = this.nameL2;
    
    this.descL1 = parameter.descLang(languages[0].key);
    
    this.newDescL1 = this.descL1;
    
    this.descL2 = parameter.descLang(languages[1].key);
    
    this.newDescL2 = this.descL2;
    
    this.lang1 = $translate.instant(languages[0].name);
  
    this.lang2 = $translate.instant(languages[1].name);
  
    this.lang1key = languages[0].key;
  
    this.lang2key = languages[1].key;
    
    if (parameter.stringkeyunit){
	    this.unitL1=parameter.unitLang(languages[0].key);
	    this.unitL2=parameter.unitLang(languages[1].key);
    }
    
    this.unit = parameter.stringkeyunit > 0;
    
    this.titlefield = parameter.id_field;
    
    

  
  
    this.edit = function(field){
    	this.activeField = field;
	    thisController.newNameL1 = thisController.nameL1;
	    thisController.newNameL2 = thisController.nameL2;
	    thisController.newDescL1 = thisController.descL1;
	    thisController.newDescL2 = thisController.descL2;
	    thisController.newFormula = thisController.formula;
    };
  
  
    
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
  	    		if (formula.ok){
	  	  	    	var tParameter = {parameterid : parameter.id};
	  	    		tParameter.formula = formula.formula;
	  	    		thisController.formula = formula.formula;
	  	    		var promise = avSampleTypeService.updateParameter(tParameter);
	  			    promise.then(
	  			    	function(){
	  			    		reload();
	  			    	},
	  			    	function(dings){
	  			    		console.log("error");
	  			    		reload();
	  			    	}
	  			    );
  	    		}
  	    	}, 
  	    	function () {
  	    		// dismissed
  	    	}
  	    );
	};

  	
	this.showFormula = function(){
		return !this.titlefield;
	}
    
  
  	this.setHidden = function(){
	    var tempParameter = { 
	    		parameterid : parameter.id, 
	    		hidden      : thisController.hidden
	    };
	    var promise = avSampleTypeService.updateParameter(tempParameter);
 	    promise.then(function(){
 	    	reload();
 	    },function(){
 	    	console.log("error");
 	    });
  	};
  
  
  
  	this.setCompulsory = function(){
  		var tempParameter = { 
  			parameterid : parameter.id,
			compulsory  : thisController.compulsory};
  		var promise = avSampleTypeService.updateParameter(tempParameter);
  		promise.then(function(){
  			reload();
  		},function(){
  			console.log("error");
  		});
  	};
  	
  	
  	
    this.keyUp = function(keyCode, value, languageKey) {
  	    if (keyCode === 13) {				// Return key pressed
  	    	var tParameter = {parameterid:parameter.id};
  	    	if (thisController.activeField === 'DL1' || thisController.activeField === 'DL2'){
  	    		tParameter.description = {};
  	    		tParameter.description[languageKey] = value;
  	    	} 
  	    	if (thisController.activeField === 'NL1' || thisController.activeField === 'NL2'){
  	    		tParameter.name = {};
  	    		tParameter.name[languageKey] = value;
  	    	}
  		  	var promise = avSampleTypeService.updateParameter(tParameter);
		    promise.then(function(){
		    	reload();
		    },function(){
		    	console.log("error");
		    });
	    }
	    if (keyCode === 27) {		// Escape key pressed
		    delete thisController.activeField;
	    }
    };
    
   
    var reload = function() {
    	var current = $state.current;
  	  	var params = angular.copy($stateParams);
  	  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };


}

angular.module('unidaplan').controller('editSingleSTParameterController', 
		['$state','$uibModal','$stateParams','$translate','avSampleTypeService','parameter',
		 'restfactory','sampleService','languages',editSingleSTParameterController]);

})();