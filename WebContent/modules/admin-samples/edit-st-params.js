(function(){
  'use strict';

function editSTParamsController($state,$uibModal,$stateParams,$translate,
		avParameters,restfactory,sampleService,parameterGrp,languages,avSampleTypeService){
  

    var thisController = this;
  
    var activeParameter = {};
    
    this.parameters = parameterGrp.parameters.sort(function(a,b){
    	return a.pos-b.pos;
    });

    this.strings = parameterGrp.strings;
  
    this.sampletype = parameterGrp.sampletype;
    
    this.sampletypenamef = parameterGrp.sampletypenamef;
  
    this.languages = languages;
          
    this.lang1 = $translate.instant(languages[0].name);
  
    this.lang2 = $translate.instant(languages[1].name);
    
    this.NL1 = { data    : { value: parameterGrp.nameLang(languages[0].key) },
                 editing : ($stateParams.newParamGrp === 'true')                                                
               }
  
    this.NL2 = { data    : { value: parameterGrp.nameLang(languages[1].key) },
            editing : false                                                
          }
  
  
    
    this.down = function(index){  // exchange two parameter positions
        var newPositions = [];
        newPositions.push({"id":thisController.parameters[index].id,"position":thisController.parameters[index+1].pos});
        newPositions.push({"id":thisController.parameters[index+1].id,"position":thisController.parameters[index].pos});
        var promise = avSampleTypeService.changeOrderSTParameters(newPositions);
        promise.then(reload,error);
    };
    
    
    
    this.getGrpName = function(grp,lang){
	    key2string.key2stringWithLangStrict(grp.name,thisController.strings,lang);
    };
    
  
  
  	this.setHidden = function(parameter){
	    var tempParameter = { 
	    		parameterid : parameter.id, 
	    		hidden : parameter.hidden
	    };
	    var promise = avSampleTypeService.updateParameter(tempParameter);
	    promise.then(reload,error);
  	};
  
  
  
  	this.setCompulsory = function(parameter){
  		var tempParameter={ 
  			parameterid : parameter.id,
			compulsory : parameter.compulsory};
  		var promise= avSampleTypeService.updateParameter(tempParameter);
        promise.then(reload,error);
  	};
  	
  	
  
	this.setIDField = function(parameter){
  		var tempParameter = { 
  			parameterid : parameter.id,
			id_field : parameter.id_field};
  		var promise= avSampleTypeService.updateParameter(tempParameter);
        promise.then(reload,error);
  	};
  	
  	
  
	this.submitParameter = function(){
		this.editmode = false;
	  	var tempParameter = {parameterid:activeParameter.id, name:{}};
	  	if (activeParameter.editNL1){
	  		tempParameter.name[languages[0].key] = activeParameter.newParameterNameL1;
	  		activeParameter.editNL1 = false;
	  	} else {
	  		tempParameter.name[languages[1].key] = activeParameter.newParameterNameL2;
	  		activeParameter.editNL2 = false;
	  	}
	  	var promise = avSampleTypeService.updateParameter(tempParameter);
        promise.then(reload,error);
	};
  
  
  
  	this.performAction = function(parameter,action){
  		// actions are defined in av-sampletype-service.getSTypeParams
  		if (action.action === "edit"  && !action.disabled){
  			$state.go('editSingleSTParameter',{parameterID:parameter.id});
  		}
  		if (action.action === "hide"  && !action.disabled){
  			var promise = avSampleTypeService.updateParameter({parameterid:parameter.id,hidden:true});
  			promise.then(reload,error);
  		}
  		if (action.action === "show"  && !action.disabled){
  			var promise = avSampleTypeService.updateParameter({parameterid:parameter.id,hidden:false});
  			promise.then(reload,error);
  		}
  		if (action.action === "delete" && !action.disabled) {
  			var promise = avSampleTypeService.deleteSTParameter(parameter.id);
  	        promise.then(reload,error);
  		}
  		if (action.action === "move" && !action.disabled) {
  			var promise = avSampleTypeService.moveParameterToGrp(parameter.id,action.destination);
  			promise.then(reload,error);
  		}
  		if (action.action === "title") {
  			thisController.setIDField(parameter);
  		}
  	};
  	
  	
  
	this.addParameter = function () {
		var modalInstance = $uibModal.open({
		    animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser.html',
		    controller: 'modalParameterChoser as mParameterChoserCtrl',
		    resolve: {
		    	mode		  	: function(){return 'immediate'; },
		    	avParameters    : function(){return avParameters; },
		    	parameters		: function(){return []; }
			}
		});
		modalInstance.result.then(
			function (result) {  // get the new Parameterlist + Info if it has changed from Modal.  
		        if (result.chosen.length>0){
		        	var promise = avSampleTypeService.AddSampletypePGParameters(thisController.sampletype,
		        	parameterGrp.id,result.chosen);
		    		promise.then(reload,error);		    	  
		    	}
		    }, function () {
		    	console.log('Strange Error: Modal dismissed at: ' + new Date());
		    });
	};
	
	
	
	this.updateNL = function(parameter){
	    var name = thisController.NL1.data.value;
	    var promise = avSampleTypeService.updateParamGrp(name, langKey, parameterGrp.id); 
        promise.then(reload,error);
	}

	
  
    this.keyUpParameter = function(keyCode,parameter) {
		if (keyCode===13) {				// Return key pressed
			thisController.submitParameter();
		}
		if (keyCode===27) {		// Escape key pressed
			parameter.editNL1 = false;
			parameter.editNL2 = false;
		}
    };

  
    
	this.setIDField = function(parameter){
  		var tempParameter = { 
  			parameterid : parameter.id,
			id_field : true};
  		var promise = avSampleTypeService.updateParameter(tempParameter);
  		promise.then(reload,error);
  	};
    
    
  
    this.up = function(index){  // exchange two parameter positions
	    var newPositions = [];
	    newPositions.push({"id":thisController.parameters[index-1].id,"position":thisController.parameters[index].pos});
	    newPositions.push({"id":thisController.parameters[index].id,"position":thisController.parameters[index-1].pos});
	    var promise = avSampleTypeService.changeOrderSTParameters(newPositions);
	    promise.then(reload,error);
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

angular.module('unidaplan').controller('editSTParamsController', 
		['$state','$uibModal','$stateParams','$translate','avParameters',
		 'restfactory','sampleService','parameterGrp','languages','avSampleTypeService',editSTParamsController]);

})();