(function(){
    'use strict';

function editSampleParamGrpsController($state,$uibModal,$stateParams,$translate,
			avParameters,restfactory,sampleService,sampleType,languages,avSampleTypeService){
 
    var thisController = this;
    
	this.parametergrps = sampleType.parametergrps.sort(function(a,b){return a.pos-b.pos});
  
	this.strings = sampleType.strings;
  
	this.titleparameters = sampleType.titleparameters;
  
	this.languages = languages;

	this.lang1key = languages[0].key;
 
	this.lang2key = languages[1].key;
	
    this.lang1 = $translate.instant(languages[0].name);
    
    this.lang2 = $translate.instant(languages[1].name);
    
    this.pNameL1 = { editing : $stateParams.newSampletype, 
                     data    : {value: sampleType.nameLang(languages[0].key)},
                     field   : "name", 
                     lang    : languages[0].key,
                   };
  
    this.pNameL2 = { editing : false, 
                     data    : {value: sampleType.nameLang(languages[1].key)},
                     field   : "name",
                     lang    : languages[1].key,
                   };
     
    this.pDescL1 = { editing : false, 
                     data    : {value: sampleType.descLang(languages[0].key)},
                     field   : "description",
                     lang    : languages[0].key,
                   };
     
    this.pDescL2 = { editing : false, 
                     data    : {value: sampleType.descLang(languages[1].key)},
                     field   : "description",
                     lang    : languages[1].key,
                   };
      
    thisController.useAsParam = sampleType.useAsParam;
  
  
  
    this.addParameter = function () {
		var modalInstance = $uibModal.open({
		    animation: false,
		    templateUrl: 'modules/modal-parameter-choser/modal-parameter-choser.html',
		    controller: 'modalParameterChoser as mParameterChoserCtrl',
		    resolve: {
		    	mode		     : function(){return 'immediate'; },
		    	avParameters : function(){return avParameters; },
		    	parameters   : function(){return []; }
			}
		});
		modalInstance.result.then(
			function (result) {  // get the new Parameterlist + Info if it has changed from Modal.  
		        if (result.chosen.length > 0){
        		        	var promise = avSampleTypeService.AddTitleParameters(sampleType.id,result.chosen);
        		        	promise.then(reload,error); 
		    	    }
		    }, function () {
		    	    console.log('Strange Error: Modal dismissed at: ' + new Date());
		    }
		);
    };
	
	
	
	this.changeField = function(parameter){
	    parameter.parameter.editing = false;
	    var promise = avSampleTypeService.updateSampleTypeData(sampleType.id,parameter.parameter);
	    promise.then(reload,error);
	};
	
	

	this.down = function(index){
		var id1 = thisController.parametergrps[index].id;
		var id2 = thisController.parametergrps[index+1].id;
		var pos1 = thisController.parametergrps[index+1].pos;
		var pos2 = thisController.parametergrps[index].pos;
		var promise = avSampleTypeService.exPosSTParamGrp(id1,pos1,id2,pos2);
		promise.then(reload,error)
	};

  
  
	this.getGrpName = function(grp,lang){
		key2string.key2stringWithLangStrict(grp.name,thisController.strings,lang)
	};
  
  
  
	this.keyUp = function(keyCode,field) {
  		if (keyCode === 13) {				// Return key pressed
  			this.changeField(field);
  		}
		if (keyCode === 27) {		// Escape key pressed
			thisController.editNL1 = false;
			thisController.editNL2 = false;
			thisController.editDL1 = false;
			thisController.editDL2 = false;
			thisController.newNameL1 = sampleType.nameLang(languages[0].key);
			thisController.newNameL2 = sampleType.nameLang(languages[1].key);
			thisController.newDescL1 = sampleType.descLang(languages[0].key);
			thisController.newDescL2 = sampleType.descLang(languages[1].key);
		}
  	};
  
  
  	 
	this.keyUpPG = function(keyCode) {
		if (keyCode === 13) {				// Return key pressed
			this.addParameterGroup();
		}
		if (keyCode === 27) {		// Escape key pressed
			this.editmode=false;
		}
	};
	
	
	
	this.newParameterGroup = function(){
         // add a new ParameterGroup to the database.
        var name = {}
        name[languages[0].key] = $translate.instant("new Parametergroup");
        name[languages[1].key] = "new Parametergroup";
        var position = 0;
        if (thisController.parametergrps){
            position = thisController.parametergrps.length + 1;
        }
        var promise = avSampleTypeService.addSTParameterGrp(sampleType.id,position,name);
        promise.then(
            function(data) {
                $state.go("editSTParams",{paramGrpID:data.data.id, newParamGrp:"true"}); // go to the new parametergroup
            },
            error
        );
	};
  
	
	
	this.parameterDown = function(index){
		var pos1 = thisController.titleparameters[index+1].pos;
		var pos2 = thisController.titleparameters[index].pos;
		thisController.titleparameters[index].pos = pos1;
		thisController.titleparameters[index+1].pos = pos2;
		var newOrder = []
		thisController.titleparameters.map(function(param){newOrder.push({id:param.id,position:param.pos})})
		var promise = avSampleTypeService.changeOrderSTParameters(newOrder);
		promise.then(reload,error);
	};
	
	
	
	this.parameterUp = function(index){
        var pos1 = thisController.titleparameters[index].pos;
        var pos2 = thisController.titleparameters[index-1].pos;
        thisController.titleparameters[index-1].pos = pos1;
        thisController.titleparameters[index].pos = pos2;
        var newOrder = []
        thisController.titleparameters.map(function(param){newOrder.push({id:param.id,position:param.pos})})
        var promise = avSampleTypeService.changeOrderSTParameters(newOrder);
        promise.then(reload,error);
    };
  
  
  

	this.performAction = function(parametergrp,action){
		if (action.action === "edit") {
			$state.go("editSTParams",{"paramGrpID":parametergrp.id})
		}
		if (action.action === "delete"  && !action.disabled) {
			var promise = avSampleTypeService.deleteSTParameterGrp(parametergrp.id);
            promise.then(reload,error);
		}
	};
  
  
  
	this.performPAction = function(parameter,action){
		if (action.action === "edit") {
			$state.go('editSingleSTParameter',{parameterID:parameter.id});
		}
		if (action.action === "delete"  && !action.disabled) {
			var promise = avSampleTypeService.deleteSTParameter(parameter.id);
			promise.then(reload,error);
		}
		if (action.action === "move"  && !action.disabled) {
			var promise = avSampleTypeService.moveParameterToGrp(parameter.id,action.destination);
			console.log("moving!");
			promise.then(reload,error);
		}
	};
  
	
  
	this.up = function(index){
        var id1 = thisController.parametergrps[index-1].id;
        var id2 = thisController.parametergrps[index].id;
        var pos1 = thisController.parametergrps[index].pos;
        var pos2 = thisController.parametergrps[index-1].pos;
        var promise = avSampleTypeService.exPosSTParamGrp(id1,pos1,id2,pos2);
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
	
	
 
};



angular.module('unidaplan').controller('editSampleParamGrpsController', ['$state','$uibModal','$stateParams','$translate',
       'avParameters','restfactory','sampleService','sampleType','languages','avSampleTypeService',editSampleParamGrpsController]);

})();