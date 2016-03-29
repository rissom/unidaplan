(function(){
  'use strict';

function editSinglePTParameterController($state,$uibModal,$stateParams,$translate,avProcessTypeService,parameter,restfactory,languages){
  

    var thisController=this;
  
    var activeParameter={};
    
    this.compulsory=parameter.compulsory;
    
    this.format=parameter.format;
        
    this.hidden=parameter.hidden;
    
    this.paramGrpID=parameter.parametergroup;
    
    this.pgnamef=parameter.pgnamef;
    
    this.processtype=parameter.processtype;
        
    this.processtypenamef=parameter.processtypenamef;
          
    this.languages=languages;
  
    this.nameL1 = parameter.nameLang(languages[0].key);
  
    this.newNameL1 = this.nameL1;
  
    this.nameL2 = parameter.nameLang(languages[1].key);

    this.newNameL2 = this.nameL2;
    
    this.descL1 = parameter.descLang(languages[0].key);
    
    this.newDescL1 = this.descL1;
    
    this.descL2 = parameter.descLang(languages[1].key);
    
    this.newDescL2 = this.descL2;
    
    this.lang1=$translate.instant(languages[0].name);
  
    this.lang2=$translate.instant(languages[1].name);
  
    this.lang1key=$translate.instant(languages[0].key);
  
    this.lang2key=$translate.instant(languages[1].key);
    
    if (parameter.stringkeyunit){
	    this.unitL1=parameter.unitLang(languages[0].key);
	    this.unitL2=parameter.unitLang(languages[1].key);
    }
  
    this.editFieldNL1=false;
  
    this.editFieldNL2=false;
    
    this.unit=parameter.stringkeyunit>0;
    
    this.titlefield=parameter.id_field;
    
    

  
  
    this.edit = function(field){
	    thisController.editFieldNL1 = (field=="NL1");
	    thisController.editFieldNL2 = (field=="NL2");
	    thisController.editFieldDL1 = (field=="DL1");
	    thisController.editFieldDL2 = (field=="DL2");
	    thisController.newNameL1=thisController.nameL1;
	    thisController.newNameL2=thisController.nameL2;
	    thisController.newDescL1=thisController.descL1;
	    thisController.newDescL2=thisController.descL2;
    };
	
  
  
  
    this.editNL1= function(){
	    thisController.editmode=true;
	    parameter.editNL1=true;
	    parameter.newParameterNameL1=parameter.nameLang(thisController.lang1key);
	    activeParameter=parameter;
    };
   
  
  
    this.editNL2= function(){
	    thisController.editmode=true;
	    parameter.editNL2=true;
	    parameter.newParameterNameL2=parameter.nameLang(thisController.lang2key);
	    activeParameter=parameter;
    };
  
  
  
  	this.setHidden=function(){
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
  
  
  
  	this.setCompulsory=function(){
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
  	    if (keyCode===13) {				// Return key pressed
  	    	var tParameter={parameterid:parameter.id};
  	    	if (thisController.editFieldDL1 || thisController.editFieldDL2){
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

  
  
    var reload=function() {
    	var current = $state.current;
  	  	var params = angular.copy($stateParams);
  	  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
    };


}

angular.module('unidaplan').controller('editSinglePTParameterController', 
		['$state','$uibModal','$stateParams','$translate','avProcessTypeService','parameter',
		 'restfactory','languages',editSinglePTParameterController]);

})();