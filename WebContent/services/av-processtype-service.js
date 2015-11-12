(function(){
'use strict';

var avProcessTypeService = function (restfactory,$q,key2string,$translate,languages) {
	// get the available processtypes and their names and recipes.

    var thisController=this;

	
    
	this.AddProcesstypePGParameters=function(processtype,paramgrp,parameters){
		var tempObj={
			processtypeid 	 : processtype,
			parametergroupid : paramgrp,
			parameterids     : parameters,
		};
		return restfactory.POST('add-pt-pg-parameters',tempObj);
	}
	
	
	
	this.getProcessType = function(process,pTypes) {
		var processTypeName
		  angular.forEach(pTypes,function(ptype) {
			if (process.processtype==ptype.id){
				processTypeName=ptype.namef();
			}
	      })
		return processTypeName; 
	}

	
	
	this.updateParamGrp = function (name, language, paramgrpid){
		return restfactory.PUT("update-pt-paramgrp",{"newname":name, "paramgrpid":paramgrpid, "language":language})
	}
	
	
	
	this.updateParameter = function (parameter){
		return restfactory.PUT("update-pt-parameter",parameter);
			
	}
	
	
	
	this.getPTypeParamGrps = function(processTypeID) {
		var defered=$q.defer();
	    var promise = restfactory.GET("process-type-param-grps?processtypeid="+processTypeID);
	    promise.then(function(rest) {
	    	thisController.processType = rest.data;
	    	thisController.strings = rest.data.strings;
	    	thisController.processType.nameLang=function(lang){
    			return (key2string.key2stringWithLangStrict(thisController.processType.name,thisController.strings,lang))
	    	}
	    	thisController.processType.descLang=function(lang){
    			return (key2string.key2stringWithLangStrict(thisController.processType.description,thisController.strings,lang))
	    	}
	    	angular.forEach(thisController.processType.parametergrps,function(ptgrp) {
	    		ptgrp.namef=function(){
	    			return (key2string.key2string(ptgrp.stringkey,thisController.strings))
	    		}
	    		ptgrp.nameLang=function(lang){
	    			return (key2string.key2stringWithLangStrict(ptgrp.stringkey,thisController.strings,lang))
	    		}
	    		
	         })
	    	defered.resolve(thisController.processType)	    	
		    }, function(rest) {
			console.log("Error loading processtypes");
		 });
	    return defered.promise;
	}
	
	
	
	this.changeOrderPTParameters=function(newPositions){
		return restfactory.PUT("change-order-pt-parameters",newPositions)
	}
	
	
	
	this.getProcessRecipes = function(process,pTypes){
		var recipes=[];
		angular.forEach(pTypes,function(ptype) {
			if (process.processtype==ptype.id){
				recipes=ptype.recipes;
			}
	      })
		return recipes
	}
	
	
	
	this.addPTParameterGrp = function(processTypeID,position,name){
		var temp={"processtypeid":processTypeID,"position":position,"name":name};
		return restfactory.POST("add-pt-parameter-grp",temp);
	}
	
	
	
	this.deletePTParameterGrp=function(id){
		return restfactory.DELETE("delete-pt-parameter-grp?id="+id);
	}
	
	
	
	this.exPosPTParamGrp=function(id1,pos1,id2,pos2){
		var jsonObj={"id1":id1, "id2":id2, "pos1":pos1, "pos2":pos2};
		return restfactory.POST ("exchange-pos-pt-parameter-grp",jsonObj);
	};

	
	
	this.getProcessTypes = function() {
        var defered=$q.defer();
        var thisController=this;
	    var promise = restfactory.GET("available-processtypes");
	    promise.then(function(rest) {
	    	thisController.processTypes = rest.data.processes;
	    	angular.forEach(thisController.processTypes,function(ptype) {
	    		ptype.namef=function(){
	    			return (key2string.key2string(ptype.name,thisController.strings))
	    		}
	    		ptype.nameLang=function(lang){
	    			return (key2string.key2stringWithLangStrict(ptype.name,thisController.strings,lang))
	    		}
	    		ptype.descf=function(){
	    			return (key2string.key2string(ptype.description,thisController.strings))
	    		}
	    		ptype.descLang=function(lang){
	    			return (key2string.key2stringWithLangStrict(ptype.description,thisController.strings,lang))
	    		}
	    		angular.forEach(ptype.recipes, function(recipe) {
	    			recipe.namef=function(){
	    				return (key2string.key2string(recipe.name,thisController.strings));
	    			}
	    		})
	         })
	         thisController.strings = rest.data.strings;
	    	thisController.loaded=true;
	    	defered.resolve(thisController.processTypes)	    	
		    }, function(rest) {
			console.log("Error loading processtypes");
		 });
	    return defered.promise;
	}

	
	
	this.getPTypeParams=function(paramGrpID){
		var defered=$q.defer();
        var thisController=this;
	    var promise = restfactory.GET("process-type-params?paramgrpid="+paramGrpID);
	    promise.then(function(rest) {
	    	thisController.paramGrp = rest.data;
	    	thisController.paramGrp.nameLang=function(lang){
    			return (key2string.key2stringWithLangStrict(thisController.paramGrp.name,thisController.paramGrp.strings,lang));
	    	}
	    	angular.forEach(thisController.paramGrp.parameters,function(parameter) {
	    		parameter.namef=function(){
	    			return (key2string.key2string(parameter.name,thisController.paramGrp.strings));
	    		}
	    		parameter.nameLang=function(lang){
	    			return (key2string.key2stringWithLangStrict(parameter.name,thisController.paramGrp.strings,lang));
	    		}
	    		parameter.unitf=function(){
	    			return (key2string.key2string(parameter.stringkeyunit,thisController.paramGrp.strings));
	    		}
	    		parameter.unitLang=function(lang){
	    			return (key2string.key2stringWithLangStrict(parameter.stringkeyunit,thisController.paramGrp.strings,lang));
	    		}
	         })
	         
	    	defered.resolve(thisController.paramGrp)	    	
		    }, function(rest) {
			console.log("Error loading parametergroup");
		 });
	    return defered.promise;
	}

	
	
	this.deletePTParameter=function(id){
		return restfactory.DELETE("delete-PT-Parameter?id="+id);
	}
	
	
	
	this.updateProcessTypeData=function(processtypeID,field,value,lang){
		var tempObj={"processtypeid":processtypeID,"field":field,"newvalue":value,"lang":lang};
		console.log ("tempObj",tempObj);
		return restfactory.POST('update-process-type-data',tempObj);
	}
}


angular.module('unidaplan').service('avProcessTypeService', ['restfactory','$q','key2string','$translate','languages',avProcessTypeService]);

})();