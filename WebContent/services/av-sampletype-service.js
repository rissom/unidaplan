(function(){
'use strict';

var avSampleTypeService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.
	
	
	var thisController=this;
	
	 // return the translated name string of a type for a sample
	  this.getType=function(sample,types){
		var typeName
		  angular.forEach(types,function(type) {
			if (sample.typeid==type.id){
			    typeName=type.namef();
			}
	      })
		return typeName;
	  }
	  
	  
	  
	this.AddSampletypePGParameters=function(sampletype,paramgrp,parameters){
		var tempObj={
			sampletypeid 	 : sampletype,
			parametergroupid : paramgrp,
			parameterids     : parameters,
		};
		return restfactory.POST('add-st-pg-parameters',tempObj);
	}
		
	
	
	this.changeOrderSTParameters=function(newPositions){
		return restfactory.PUT("change-order-st-parameters",newPositions)
	}
	
	
	
	this.getSampleTypeParamGrps = function(sampleTypeID){
		var defered=$q.defer();
	    var promise = restfactory.GET("sample-type-param-grps?sampletypeid="+sampleTypeID);
	    promise.then(function(rest) {
	    	thisController.sampleType = rest.data;
	    	thisController.strings = rest.data.strings;
	    	thisController.sampleType.nameLang=function(lang){
    			return (key2string.key2stringWithLangStrict(thisController.sampleType.string_key,thisController.strings,lang))
	    	}
	    	thisController.sampleType.descLang=function(lang){
    			return (key2string.key2stringWithLangStrict(thisController.sampleType.description,thisController.strings,lang))
	    	}
	    	angular.forEach(thisController.sampleType.parametergrps,function(stgrp) {
	    		stgrp.namef=function(){
	    			return (key2string.key2string(stgrp.stringkey,thisController.strings))
	    		}
	    		stgrp.nameLang=function(lang){
	    			return (key2string.key2stringWithLangStrict(stgrp.stringkey,thisController.strings,lang))
	    		}

	    		stgrp.actions=[{action:"edit",name:$translate.instant("edit")},
	    		               {action:"delete",name:$translate.instant("delete"),disabled:!stgrp.deletable}
	    					  ]
	         })
	    	defered.resolve(thisController.sampleType)	    	
		    }, function(rest) {
			console.log("Error loading sampletypes");
		 });
	    return defered.promise;
	}
	
	
	this.getSampleTypes = function() {
        var defered=$q.defer();
    	var thisController=this;
    	var promise = restfactory.GET("sampletypes");
    	promise.then(function(rest) {
	    	thisController.sampleTypes = rest.data.sampletypes;
		    thisController.strings = rest.data.strings;
	    	angular.forEach(thisController.sampleTypes,function(sampleType) {
	    		sampleType.namef=function(){
					return (key2string.key2string(sampleType.string_key,thisController.strings))
				};
	    		sampleType.nameLang=function(lang){
					return (key2string.key2stringWithLangStrict(sampleType.string_key,thisController.strings,lang))
				};
	    		sampleType.descf=function(){
					return (key2string.key2string(sampleType.description,thisController.strings))
				};
	    		sampleType.descLang=function(lang){
					return (key2string.key2stringWithLangStrict(sampleType.description,thisController.strings,lang))
				};
	    		sampleType.actions= [ {name: $translate.instant("edit")},
	    		                      {name: $translate.instant("delete") , disabled:!sampleType.deletable}
	    						    ]
				angular.forEach(sampleType.recipes, function(recipe) {
					recipe.namef=function(){
						return (key2string.key2string(recipe.name,thisController.strings));
					}
				})
	      })
    	  defered.resolve(thisController.sampleTypes)		
	    	
    	}, function(rest) {
    		console.log("Error loading sampletypes");
    	});
		return defered.promise;
	}
	
     
	
	this.getSTypeParams = function(paramGrpID){
		var defered=$q.defer();
        var thisController=this;
	    var promise = restfactory.GET("sample-type-params?paramgrpid="+paramGrpID);
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
	    		parameter.actions=[{action:"edit",name:$translate.instant("edit")},
	    		                   {action:"delete",name:$translate.instant("delete"),disabled:!parameter.deletable}]
	         })
	         
	    	defered.resolve(thisController.paramGrp)	    	
		    }, function(rest) {
			console.log("Error loading parametergroup");
		 });
	    return defered.promise;
	}

	
	
	this.addSTParameterGrp = function(sampleTypeID,position,name){
		var temp={"sampletypeid":sampleTypeID,"position":position,"name":name};
		return restfactory.POST("add-st-parameter-grp",temp);	
	}
	
	
	
	this.deleteSTParameter=function(id){
		return restfactory.DELETE("delete-st-parameter?id="+id);
	}
	
	
	
	this.deleteSTParameterGrp=function(id){
		return restfactory.DELETE("delete-st-parameter-grp?id="+id);
	}

	
	
	this.exPosSTParamGrp=function(id1,pos1,id2,pos2){
		var jsonObj={"id1":id1, "id2":id2, "pos1":pos1, "pos2":pos2};
		return restfactory.POST ("exchange-pos-st-parameter-grp",jsonObj);
	};	
	
	
	
	this.updateSampleTypeData=function(sampletypeID,field,value,lang){
		var tempObj={"sampletypeid":sampletypeID,"field":field,"newvalue":value,"lang":lang};
		return restfactory.POST('update-sample-type-data',tempObj);
	}
	
	
	
	this.updateParamGrp = function (name, language, paramgrpid){
		return restfactory.PUT("update-st-paramgrp",{"newname":name, "paramgrpid":paramgrpid, "language":language})
	}
	
	
}


angular.module('unidaplan').service('avSampleTypeService', ['restfactory','$q','$translate','key2string',avSampleTypeService]);

})();