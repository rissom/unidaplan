(function(){
  'use strict';

function editSTParamsController($state,$modal,$stateParams,$translate,
		avParameters,restfactory,sampleService,parameterGrp,languages,avSampleTypeService){
  

    var thisController=this;
  
    var activeParameter={};
    
    this.parameters=parameterGrp.parameters.sort(function(a,b){
    	return a.pos-b.pos;
    });
  
    this.strings=parameterGrp.strings;
  
    this.sampletype=parameterGrp.sampletype;
  
    this.languages=languages;
  
    this.nameL1 = parameterGrp.nameLang(languages[0].key);
  
    this.newNameL1 = parameterGrp.nameLang(languages[0].key);
  
    this.nameL2 = parameterGrp.nameLang(languages[1].key);

    this.newNameL2 = parameterGrp.nameLang(languages[1].key);
    
    this.lang1=$translate.instant(languages[0].name);
  
    this.lang2=$translate.instant(languages[1].name);
  
    this.lang1key=$translate.instant(languages[0].key);
  
    this.lang2key=$translate.instant(languages[1].key);
  
    this.editFieldNL1=false;
  
    this.editFieldNL2=false;
      
  
  
    this.getGrpName=function(grp,lang){
	    key2string.key2stringWithLangStrict(grp.name,thisController.strings,lang);
    };
  

  
  
    this.edit = function(field){
	    thisController.editFieldNL1 = (field=="NL1");
	    thisController.editFieldNL2 = (field=="NL2");
	    thisController.newNameL1=thisController.nameL1;
	    thisController.newNameL2=thisController.nameL2;
    };
	
  
  
  
    this.editNL1= function(parameter){
	    thisController.editmode=true;
	    parameter.editNL1=true;
	    parameter.newParameterNameL1=parameter.nameLang(thisController.lang1key);
	    activeParameter=parameter;
    };
   
  
  
    this.editNL2= function(parameter){
	    thisController.editmode=true;
	    parameter.editNL2=true;
	    parameter.newParameterNameL2=parameter.nameLang(thisController.lang2key);
	    activeParameter=parameter;
    };
  
  
  
  	this.setHidden=function(parameter){
	    var tempParameter={ 
	    		parameterid : parameter.id, 
	    		hidden : parameter.hidden
	    };
	    var promise= avSampleTypeService.updateParameter(tempParameter);
 	    promise.then(function(){
 	    	reload();
 	    },function(){
 	    	console.log("error");
 	    });
  	};
  
  
  
  	this.setCompulsory=function(parameter){
  		var tempParameter={ 
  			parameterid : parameter.id,
			compulsory : parameter.compulsory};
  		console.log(tempParameter);
  		var promise= avSampleTypeService.updateParameter(tempParameter);
  		promise.then(function(){
  			reload();
  		},function(){
  			console.log("error");
  		});
  	};
  
  
  
	this.submitParameter=function(){
		this.editmode=false;
	  	var tempParameter={parameterid:activeParameter.id, name:{}};
	  	if (activeParameter.editNL1){
	  		tempParameter.name[languages[0].key]=activeParameter.newParameterNameL1;
	  		activeParameter.editNL1=false;
	  	} else {
	  		tempParameter.name[languages[1].key]=activeParameter.newParameterNameL2;
	  		activeParameter.editNL2=false;
	  	}
	  	var promise= avSampleTypeService.updateParameter(tempParameter);
	  	promise.then(function(){
	  		reload();
	  	},function(){
	  		console.log("error");
	  	});
	};
  
  
  
  	this.performAction=function(parameter,action){
  		// actions are defined in av-sampletype-service.getSTypeParams
  		if (action.action==="edit"){
  			console.log("not implemented yet");
  		}
  		if (action.action==="delete") {
  			var promise = avSampleTypeService.deleteSTParameter(parameter.id);
  			promise.then(function(){
  				reload();
  			},function(){
  				console.log("error");
  			});
  		}
  	};
  
  
  
	this.addParameter = function () {
		var modalInstance = $modal.open({
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
		        	var promise=avSampleTypeService.AddSampletypePGParameters(thisController.sampletype,
		        	parameterGrp.id,result.chosen);
		    		promise.then(function(){
		    			reload();
		    		});		    	  
		    	}
		    }, function () {
		    	console.log('Strange Error: Modal dismissed at: ' + new Date());
		    });
	};
	
  
  
    this.keyUp = function(keyCode,name,language) {
  	    if (keyCode===13) {				// Return key pressed
	  	    var promise=avSampleTypeService.updateParamGrp(name, language, parameterGrp.id);	
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

  
  
    this.keyUpParameter = function(keyCode,parameter) {
		if (keyCode===13) {				// Return key pressed
			console.log("Return pressed");
			thisController.submitParameter();
		}
		if (keyCode===27) {		// Escape key pressed
			parameter.editNL1=false;
			parameter.editNL2=false;
		}
    };
  
  
  
    this.down=function(index){  // exchange two parameter positions
	    var newPositions=[];
	    newPositions.push({"id":thisController.parameters[index].id,"position":thisController.parameters[index+1].pos});
	    newPositions.push({"id":thisController.parameters[index+1].id,"position":thisController.parameters[index].pos});
	    var promise = avSampleTypeService.changeOrderSTParameters(newPositions);
	    promise.then(function(){
	    	reload();
	    },function(){
	    	console.log("error");
	    })
    };

  
  
    this.up=function(index){  // exchange two parameter positions
	    var newPositions=[];
	    newPositions.push({"id":thisController.parameters[index-1].id,"position":thisController.parameters[index].pos});
	    newPositions.push({"id":thisController.parameters[index].id,"position":thisController.parameters[index-1].pos});
	    var promise = avSampleTypeService.changeOrderSTParameters(newPositions);
	    promise.then(function(){
	    	reload();
	    },function(){
	    	console.log("error");
	    })
    };
  
  
  
    var reload=function() {
    	var current = $state.current;
  	  	var params = angular.copy($stateParams);
  	  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };


}

angular.module('unidaplan').controller('editSTParamsController', 
		['$state','$modal','$stateParams','$translate','avParameters',
		 'restfactory','sampleService','parameterGrp','languages','avSampleTypeService',editSTParamsController]);

})();