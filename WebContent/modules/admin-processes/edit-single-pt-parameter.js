(function(){
  'use strict';

function editSinglePTParameterController($state,$uibModal,$stateParams,$translate,avProcessTypeService,parameter,restfactory,languages){
  

    var thisController = this;
    
    this.compulsory = parameter.compulsory;
    
    this.format = parameter.format;
    
    this.formula = parameter.formula;
        
    this.hidden = parameter.hidden;
    
    this.paramGrpID = parameter.parametergroup;
    
    this.pgnamef = parameter.pgnamef;
    
    this.processtype = parameter.processtype;
        
    this.processtypenamef = parameter.processtypenamef;
          
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
  
    this.lang1key = $translate.instant(languages[0].key);
  
    this.lang2key = $translate.instant(languages[1].key);
    
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
	  	    		var promise = avProcessTypeService.updateParameter(tParameter);
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

  
  
  
  
  	this.setHidden = function(){
	    var tempParameter={ 
	    		parameterid : parameter.id, 
	    		hidden : thisController.hidden
	    };
	    var promise= avProcessTypeService.updateParameter(tempParameter);
 	    promise.then(function(){
 	    	reload();
 	    },function(){
 	    	console.log("error");
 	    });
  	};
  
  
  
  	this.setCompulsory = function(){
  		var tempParameter={ 
  			parameterid : parameter.id,
			compulsory  : thisController.compulsory};
  		var promise= avProcessTypeService.updateParameter(tempParameter);
  		promise.then(function(){
  			reload();
  		},function(){
  			console.log("error");
  		});
  	};
  	
  
  
  
    this.keyUp = function(keyCode,value,language) {
  	    if (keyCode === 13) {				// Return key pressed
  	    	var tParameter={parameterid:parameter.id};
  	    	if (thisController.activeField === 'DL1' || thisController.activeField === 'DL2'){
  	    		tParameter.description={};
  	    		tParameter.description[language]=value;
  	    	} else{
  	    		tParameter.name={};
  	    		tParameter.name[language]=value;
  	    	}
  		  	var promise = avProcessTypeService.updateParameter(tParameter);
		    promise.then(function(){
		    	reload();
		    },function(){
		    	console.log("error");
		    });
	    }
	    if (keyCode===27) {		// Escape key pressed
		    thisController.editmode=false;
	    }
    };

  
  
    var reload = function() {
    	var current = $state.current;
  	  	var params = angular.copy($stateParams);
  	  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };


}

angular.module('unidaplan').controller('editSinglePTParameterController', 
		['$state','$uibModal','$stateParams','$translate','avProcessTypeService','parameter',
		 'restfactory','languages',editSinglePTParameterController]);

})();