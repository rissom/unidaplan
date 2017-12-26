(function(){
'use strict';

var avProcessTypeService = function (restfactory,$q,key2string,$translate,languages) {
	// get the available processtypes and their names and recipes.

    var thisController = this;

	
    
	this.AddProcesstypePGParameters = function(processtype,paramgrp,parameters){
		var tempObj = {
			processtypeid 	 : processtype,
			parametergroupid : paramgrp,
			parameterids     : parameters,
		};
		return restfactory.POST('add-pt-pg-parameters',tempObj);
	};
	
	
	
	this.addPTParameterGrp = function(processTypeID,position,name){
		var temp = {"processtypeid":processTypeID,"position":position,"name":name};
		return restfactory.POST("add-pt-parameter-grp", temp);
	};
	
	
	
	this.addProcesstypeSRParameters = function(processTypeID,chosenParams,name){
		var temp={
				processtypeid : processTypeID,
				name : name, 
				parameterids:chosenParams};
		return restfactory.POST("add-pt-sr-parameter",temp);
	};
	
	
	
	this.changeOrderPTParameters = function(newPositions){
		return restfactory.PUT("change-order-pt-parameters",newPositions);
	};
	

	
	this.deletePTParameter = function(id){
		return restfactory.DELETE("delete-pt-parameter?id="+id);
	};
	
	
	
	this.deletePTSRParameter = function(id){
		return restfactory.DELETE("delete-pt-sr-parameter?id="+id);
	};
	
	
	
	this.deletePTParameterGrp=function(id){
		return restfactory.DELETE("delete-pt-parameter-grp?id="+id);
	};
	
	
	
	this.exPosPTParamGrp = function(id1,pos1,id2,pos2){
		var jsonObj = {id1:id1, id2:id2, pos1:pos1, pos2:pos2};
		return restfactory.POST ("exchange-pos-pt-parameter-grp",jsonObj);
	};
	
	
	
	this.exPosPTSRParams = function(id1,pos1,id2,pos2){
		var jsonObj = {id1:id1, id2:id2, pos1:pos1, pos2:pos2};
		return restfactory.POST ("exchange-pos-pt-sr-parameter",jsonObj);
	};
	
	
	
	this.getProcessRecipes = function(process,pTypes){
		var recipes = [];
		angular.forEach(pTypes,function(ptype) {
			if (process.processtype==ptype.id){
				recipes=ptype.recipes;
			}
	      });
		return recipes;
	};


	
	this.getProcessType = function(process,pTypes) {
		var processTypeName = "processtype not found";
		if (pTypes){
		  	angular.forEach(pTypes,function(ptype) {
		  		if (process.processtype == ptype.id){
		  			processTypeName = ptype.namef();
		  		}
		  	});
		} else {
			console.log("no ptypes")
		}
		return processTypeName; 
	};

	
	
	this.getProcessTypes = function() {
        var defered=$q.defer();
	    var promise = restfactory.GET("available-processtypes");
	    promise.then(function(rest) {
	    	var strings = rest.data.strings;
	    	var processTypes = rest.data.processes;
	    	angular.forEach(processTypes,function(ptype) {
	    		ptype.namef = function(){
	    			return (key2string.key2string(ptype.name,strings));
	    		};
	    		ptype.nameLang = function(lang){
	    			return (key2string.key2stringWithLangStrict(ptype.name,strings,lang));
	    		};
	    		ptype.descf = function(){
	    			return (key2string.key2string(ptype.description,strings));
	    		};
	    		ptype.descLang = function(lang){
	    			return (key2string.key2stringWithLangStrict(ptype.description,strings,lang));
	    		};
	    		ptype.actions = [{ action  : "edit",     
	    						   namef   : function(){ return $translate.instant("edit")}},
	    		                 { action  : "duplicate",
	    					       namef   : function(){return $translate.instant("duplicate");}},
	    		                 { action  : "delete",   
	    					       namef   : function(){return $translate.instant("delete");},
	    					       disabled: !ptype.deletable}];
	    		angular.forEach(ptype.recipes, function(recipe) {
	    			recipe.namef = function(){
	    				return (key2string.key2string(recipe.name,strings));
	    			};
	    		});
	         });
	    	defered.resolve(processTypes);	    	
		    }, function(rest) {
			console.log("Error loading processtypes");
		 });
	    return defered.promise;
	};
	

	
	this.getPTypeParamGrps = function(processTypeID) {
		var defered=$q.defer();
	    var promise = restfactory.GET("process-type-param-grps?processtypeid="+processTypeID);
	    promise.then(function(rest) {
	    	thisController.processType = rest.data;
	    	var strings = rest.data.strings;
	    	thisController.processType.nameLang=function(lang){
    			return (key2string.key2stringWithLangStrict(thisController.processType.name,strings,lang));
	    	};
	    	thisController.processType.descLang=function(lang){
    			return (key2string.key2stringWithLangStrict(thisController.processType.description,strings,lang));
	    	};
	    	
	    	// get parameter groups
	    	angular.forEach(thisController.processType.parametergrps,function(ptgrp) {
	    		ptgrp.namef=function(){
	    			return (key2string.key2string(ptgrp.stringkey,strings));
	    		};
	    		ptgrp.nameLang=function(lang){
	    			return (key2string.key2stringWithLangStrict(ptgrp.stringkey,strings,lang));
	    		};
	    		ptgrp.actions=[{action:"edit",name:$translate.instant("edit")},
	    		               {action:"delete",name:$translate.instant("delete"),disabled:!ptgrp.deletable}];	    		
	         });
	    	
	    	// get sample related parameters
	    	angular.forEach(thisController.processType.samplerparams,function(sparam) {
	    		sparam.namef = function(){
	    			return (key2string.key2string(sparam.stringkey,strings));
	    		};
	    		sparam.nameLang = function(lang){
	    			return (key2string.key2stringWithLangStrict(sparam.stringkey,strings,lang));
	    		};
	    		sparam.actions=[{action:"edit",name:$translate.instant("edit")},
	    		               {action:"delete",name:$translate.instant("delete"),disabled:!sparam.deletable}];	    
	    	});
	    	defered.resolve(thisController.processType);	
		    }, function(rest) {
			console.log("Error loading processtypes");
		 });
	    return defered.promise;
	};
	
	
	
	this.getPTypeParams = function(paramGrpID){
		var defered = $q.defer();
        var thisController=this;
	    var promise = restfactory.GET("process-type-params?paramgrpid="+paramGrpID);
	    promise.then(function(rest) {
	    	thisController.paramGrp = rest.data;
	    	thisController.paramGrp.nameLang=function(lang){
    			return (key2string.key2stringWithLangStrict(thisController.paramGrp.name,thisController.paramGrp.strings,lang));
	    	};
	    	angular.forEach(thisController.paramGrp.parameters,function(parameter) {
	    		parameter.namef = function(){
	    			return key2string.key2string(parameter.name,thisController.paramGrp.strings);
	    		};
	    		parameter.nameLang = function(lang){
	    			return key2string.key2stringWithLangStrict(parameter.name,thisController.paramGrp.strings,lang);
	    		};
	    		parameter.unitf = function(){
	    			return key2string.key2string(parameter.stringkeyunit,thisController.paramGrp.strings);
	    		};
	    		parameter.unitLang = function(lang){
	    			return key2string.key2stringWithLangStrict(parameter.stringkeyunit,thisController.paramGrp.strings,lang);
	    		};
	    		// actions for the context menu. Have to be implemented in editPtParamsCtrl.performAction
	    		parameter.actions = [{action:"edit",name:$translate.instant("edit")},
	    		                     {action:"delete",name:$translate.instant("delete"), disabled:!parameter.deletable}];
	         });
	         
	    	defered.resolve(thisController.paramGrp);
		    }, function(rest) {
			console.log("Error loading parametergroup");
		 });
	    return defered.promise;
	};


	
	this.getSinglePOParameter = function(parameterID){
        var defered=$q.defer();
    	var promise = restfactory.GET("single-po-parameter?parameterid="+parameterID);
    	promise.then(function(rest) {
	    	var parameter = rest.data;
	    	var strings = rest.data.strings;
	    	if (parameter.parametergroupname){
	    		parameter.pgnamef=function(){
	    			return (key2string.key2string(parameter.parametergroupname,strings));
	    		};
	    	}
	    	parameter.processtypenamef = function(){
				return (key2string.key2string(parameter.processtypename,strings));
			};
		    parameter.namef = function(){
					return (key2string.key2string(parameter.name,strings));
				};
			parameter.nameLang = function(lang){
					return (key2string.key2stringWithLangStrict(parameter.name,strings,lang));
				};
			parameter.descf = function(){
					return (key2string.key2string(parameter.description,strings));
				};
			parameter.descLang = function(lang){
					return (key2string.key2stringWithLangStrict(parameter.description,strings,lang));
				};
			parameter.unitLang = function(lang){
					return (key2string.key2stringWithLangStrict(parameter.stringkeyunit,strings,lang));
				};
	        defered.resolve(parameter);
    	}, function(rest) {
    		console.log("Error loading processtype-parameter");
    	});
		return defered.promise;
	};
	
	
	
	this.getSinglePTypeParameter = function(parameterID){
        var defered=$q.defer();
    	var promise = restfactory.GET("single-pt-parameter?parameterid="+parameterID);
    	promise.then(function(rest) {
	    	var parameter = rest.data;
	    	var strings = rest.data.strings;
	    	if (parameter.parametergroupname){
	    		parameter.pgnamef=function(){
	    			return (key2string.key2string(parameter.parametergroupname,strings));
	    		};
	    	}
	    	parameter.processtypenamef=function(){
				return (key2string.key2string(parameter.processtypename,strings));
			};
		    parameter.namef=function(){
					return (key2string.key2string(parameter.name,strings));
				};
			parameter.nameLang=function(lang){
					return (key2string.key2stringWithLangStrict(parameter.name,strings,lang));
				};
			parameter.descf=function(){
					return (key2string.key2string(parameter.description,strings));
				};
			parameter.descLang=function(lang){
					return (key2string.key2stringWithLangStrict(parameter.description,strings,lang));
				};
//				if (parameter.stringkeyunit){
				parameter.unitLang=function(lang){
					return (key2string.key2stringWithLangStrict(parameter.stringkeyunit,strings,lang));
				};
//				}
				angular.forEach(parameter.otherparameters, function(parameter){
					parameter.namef = function(){
						return key2string.key2string(parameter.stringkeyname,strings);
					}
				});
	        defered.resolve(parameter);
    	}, function(rest) {
    		console.log("Error loading processtype-parameter");
    	});
		return defered.promise;
	};
		     
	
	
	this.updateParamGrp = function (name, language, paramgrpid){
		return restfactory.PUT("update-pt-paramgrp",{"newname":name, "paramgrpid":paramgrpid, "language":language});
	};
	
	
	
	this.updateParameter = function (parameter){
		return restfactory.PUT("update-pt-parameter",parameter);
	};
	
	
	
	this.updatePOParameter = function (parameter){
		return restfactory.PUT("update-po-parameter",parameter);
	};
	
	

	
	this.updateProcessTypeData = function(processtypeID,field,value,lang){
		var tempObj={"processtypeid":processtypeID,"field":field,"newvalue":value,"lang":lang};
		return restfactory.POST('update-process-type-data',tempObj);
	};
	
	
	
	this.updateGroupRights = function (updatedRights){
		return restfactory.PUT('update-group-rights',updatedRights);
	}	
	
	
	
	this.updateUserRights = function (updatedRights){
		return restfactory.PUT('update-user-rights',updatedRights);
	}	
	
};


angular.module('unidaplan').service('avProcessTypeService', ['restfactory','$q','key2string','$translate','languages',avProcessTypeService]);

})();