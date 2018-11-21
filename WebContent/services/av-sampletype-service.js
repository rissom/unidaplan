(function(){
'use strict';

var avSampleTypeService = function (restfactory, $q, $rootScope, $translate, key2string) {
	// restfactory is a wrapper for $html.
	
	
	var thisController = this;
	
	   
	  
	this.AddSampletypePGParameters = function(sampletype,paramgrp,parameters){
		var tempObj = {
			sampletypeid 	 : sampletype,
			parametergroupid : paramgrp,
			parameterids     : parameters,
		};
		return restfactory.POST('add-st-parameters',tempObj);
	};
	
	
	
	this.AddTitleParameters = function(sampletype,parameters){
		var tempObj={
			sampletypeid 	 : sampletype,
			parameterids     : parameters,
		};
		return restfactory.POST('add-st-parameters',tempObj);
	};
	
	
	this.changeOrderSTParameters = function(newPositions){
		return restfactory.PUT("change-order-st-parameters",newPositions);
	};
	
	
	
	this.addSTParameterGrp = function(sampleTypeID,position,name){
		var temp = {"sampletypeid":sampleTypeID,"position":position,"name":name};
		return restfactory.POST("add-st-parameter-grp",temp);	
	};
	
	
	
	this.deleteSTParameter = function(id){
		return restfactory.DELETE("delete-st-parameter?id="+id);
	};
	
	
	
	this.deleteSTParameterGrp = function(id){
		return restfactory.DELETE("delete-st-parameter-grp?id="+id);
	};

	
	
	this.exPosSTParamGrp = function(id1,pos1,id2,pos2){
		var jsonObj={"id1":id1, "id2":id2, "pos1":pos1, "pos2":pos2};
		return restfactory.POST ("exchange-pos-st-parameter-grp",jsonObj);
	};	
	
	
	
	this.getSampleTypeParamGrps = function(sampleTypeID){
		var defered = $q.defer();
	    var promise = restfactory.GET("sample-type-param-grps?sampletypeid=" + sampleTypeID);
	    promise.then(function(rest) {
	    	    thisController.sampleType = rest.data;
	    	    if (thisController.sampleType == undefined) {thisController.sampleType = {} }
	    	    thisController.strings = rest.data.strings;
	    	    thisController.sampleType.nameLang = function(lang){
	    	        return (key2string.key2stringWithLangStrict(thisController.sampleType.string_key,thisController.strings,lang));
	    	    };
	    	    thisController.sampleType.descLang=function(lang){
	    	        return (key2string.key2stringWithLangStrict(thisController.sampleType.description,thisController.strings,lang));
	    	    };
	    	    angular.forEach(thisController.sampleType.parametergrps,function(stgrp) {
	    	        stgrp.namef = function(){
	    	            return (key2string.key2string(stgrp.stringkey,thisController.strings));
	    	        };
	    	        stgrp.nameLang = function(lang){
	    	            return (key2string.key2stringWithLangStrict(stgrp.stringkey,thisController.strings,lang));
	    	        };
	    	        stgrp.actions = [{action:"edit",namef: function() { return $translate.instant("edit") }},
	    		                     {action:"delete",namef: function() { return $translate.instant("delete")},disabled:!stgrp.deletable}
	    					        ];
	        });
	    	    angular.forEach(thisController.sampleType.titleparameters,function(tparam) {
	    	        tparam.namef = function(){
	    	            return (key2string.key2string(tparam.name,thisController.strings));
	    	        };
    	    		    tparam.nameLang = function(lang){
    	    		        return (key2string.key2stringWithLangStrict(tparam.name,thisController.strings,lang));
    	    		    };
    	    		    tparam.actions = [{ action   : "edit",
	    						        namef	: function () { return $translate.instant("edit"); }
    	    		                      },
    	    		                      { action	  : "delete",
    	    		                          namef	  : function() { return $translate.instant("delete"); },
    	    		                          disabled : thisController.sampleType.titleparameters.length == 1 || !tparam.deletable
    	    		                      }];
    	    		    for (var k = 0; k < rest.data.parametergrps.length; k++){
    	    		        var actions = { action  : "move",
    	    		                        j       :  k,
    	    		                        namef   : function () {
    	    		                            var answer = "";
    	    		                            if (thisController.sampleType.parametergrps[this.j] != undefined){
    	    		                                answer = $translate.instant("move to") + " " + thisController.sampleType.parametergrps[this.j].namef();
    	    		                            }
    	    		                            return answer;
    	    		                        },
    	    		                        destination : thisController.sampleType.parametergrps[k].id
    	    		                      }
    	    		        tparam.actions.push(actions)
    	    		    }
	        });
	    	    defered.resolve(thisController.sampleType); 	
		}, function(rest) {
		        console.log("Error loading sampletypes");
		});
	    return defered.promise;
	};
	
	
	
	this.getSampleTypes = function() {
        var defered=$q.defer();
    	var promise = restfactory.GET("sampletypes");
    	promise.then(function(rest) {
	    	thisController.sampleTypes = rest.data.sampletypes;
		    thisController.strings = rest.data.strings;
	    	angular.forEach(thisController.sampleTypes,function(sampleType) {
	    		sampleType.namef = function(){
					return (key2string.key2string(sampleType.string_key,thisController.strings));
				};
	    		sampleType.nameLang = function(lang){
					return (key2string.key2stringWithLangStrict(sampleType.string_key,thisController.strings,lang));
				};
	    		sampleType.descf = function(){
					return (key2string.key2string(sampleType.description,thisController.strings));
				};
	    		sampleType.descLang = function(lang){
					return (key2string.key2stringWithLangStrict(sampleType.description,thisController.strings,lang));
				};
	    		sampleType.actions= [ {action:"edit",  namef: function() { return $translate.instant("edit")}  },
	    		                      {action:"delete",namef: function() { return $translate.instant("delete") }, disabled:!sampleType.deletable}
	    						    ];
				angular.forEach(sampleType.recipes, function(recipe) {
					recipe.namef = function(){
						return (key2string.key2string(recipe.name,thisController.strings));
					};
				});
	      });
    	  defered.resolve(thisController.sampleTypes);
	    	
    	}, function(rest) {
    	    $rootScope.log_E("Error loading sampletypes");
    	});
		return defered.promise;
	};
	
	
	
	this.getSingleSTypeParameter = function(parameterID) {
        var defered=$q.defer();
    	var promise = restfactory.GET("single-st-parameter?parameterid="+parameterID);
    	promise.then(function(rest) {
	    	var parameter = rest.data;
	    	var strings = rest.data.strings;
	    	if (parameter.parametergroupname){
	    		parameter.pgnamef = function(){
	    			return (key2string.key2string(parameter.parametergroupname,strings));
	    		};
	    	}
	    	parameter.sampletypenamef = function(){
				return (key2string.key2string(parameter.sampletypename,strings));
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
				
			angular.forEach(parameter.otherparameters, function(parameter){
				parameter.namef = function(){
					return key2string.key2string(parameter.stringkeyname,strings);
				}
			});
				
			
//			if (parameter.stringkeyunit){
				parameter.unitLang = function(lang){
					return (key2string.key2stringWithLangStrict(parameter.stringkeyunit,strings,lang));
				};
//			}
	        defered.resolve(parameter);
    	}, function(rest) {
    		console.log("Error loading sampletype-parameter");
    	});
		return defered.promise;
	}
	     
	
	
	this.getSTypeParams = function(paramGrpID){
		var defered=$q.defer();
	    var promise = restfactory.GET("sample-type-params?paramgrpid="+paramGrpID);
	    promise.then(function(rest) {
	    	thisController.paramGrp = rest.data;
	    	thisController.paramGrp.nameLang = function(lang){
    			return (key2string.key2stringWithLangStrict(thisController.paramGrp.name,thisController.paramGrp.strings,lang));
	    	};
	    	thisController.paramGrp.sampletypenamef = function(){
				return (key2string.key2string(thisController.paramGrp.sampletypename,thisController.paramGrp.strings));
			};
	    	angular.forEach(thisController.paramGrp.parameters,function(parameter) {
	    		parameter.namef=function(){
	    			return (key2string.key2string(parameter.name,thisController.paramGrp.strings));
	    		};
	    		parameter.nameLang = function(lang){
	    			return (key2string.key2stringWithLangStrict(parameter.name,thisController.paramGrp.strings,lang));
	    		};
	    		parameter.unitf = function(){
	    			return (key2string.key2string(parameter.stringkeyunit,thisController.paramGrp.strings));
	    		};
	    		parameter.unitLang = function(lang){
	    			return (key2string.key2stringWithLangStrict(parameter.stringkeyunit,thisController.paramGrp.strings,lang));
	    		};
	    		parameter.actions = [
	    		    { action :"edit",
	    		      namef  : function (){ return $translate.instant("edit")}
	    		    }];
	    		if (parameter.hidden) { 
	    				parameter.actions.push({
	    					action:"show",
	    					namef : function() {return $translate.instant("show again")}
	    				});	
	    			} else {
	    				parameter.actions.push({
	    					action :"hide",
	    					namef  : function() {return $translate.instant("hide")}
	    				});
	    			}
	    		parameter.actions.push(
	    				{  action  : "delete",
	    				   namef   : function() { return $translate.instant("delete")},
	    			       disabled: !parameter.deletable});
	    		if ("siblings" in thisController.paramGrp){
	    			thisController.paramGrp.siblings.map(function(sibling){
	    				var name = key2string.key2string(sibling.name,thisController.paramGrp.strings);
	    				parameter.actions.push(
    						{ action	  : "move",
    						  namef		  : function() { return $translate.instant("move to") + " " + name },
    						  destination : sibling.id
    						}
    					);
	    			});
	    		}
	    		if (parameter.datatype == 'string' || parameter.datatype == 'integer'){
	    			parameter.actions.push({
	    				action : 'title',
	    				namef	: function() { return $translate.instant("make titleparameter") }
	    			});
	    		}
	        });
	         
	    	defered.resolve(thisController.paramGrp);  	
	    }, function(rest) {
			console.log("Error loading parametergroup");
		});
	    return defered.promise;
	}
	
	
	
	 // return the translated name string of a type for a sample
	this.getType = function(sample,types){
		var typeName;
		 	angular.forEach(types,function(type) {
		 		if (sample.typeid == type.id){
		 			typeName = type.namef();
		 		}
		 	});
		return typeName;
	};
	  

	
	this.getAllSTParameters = function(sampleTypeID){
		var defered=$q.defer();
	    var promise = restfactory.GET("all-sample-type-params?sampletypeid="+sampleTypeID);
	    promise.then(function(rest) {
	    	var params = rest.data.parameters;
	    	var strings = rest.data.strings;
	    	var paramgrps = rest.data.parametergrps;
	    	angular.forEach(params,function(parameter) {
	    		parameter.namef = function(){
	    			return (key2string.key2string(parameter.name,strings));
	    		};
	    		parameter.nameLang = function(lang){
	    			return (key2string.key2stringWithLangStrict(parameter.name,strings,lang));
	    		};
	    		parameter.unitf = function(){
	    			return (key2string.key2string(parameter.stringkeyunit,strings));
	    		};
	    		parameter.unitLang = function(lang){
	    			return (key2string.key2stringWithLangStrict(parameter.stringkeyunit,strings,lang));
	    		};
	        });
	        angular.forEach(paramgrps,function(paramgrp) {
	        	paramgrp.namef = function(){
	    			return (key2string.key2string(paramgrp.stringkey,strings));
	    		};
	        	paramgrp.nameLang = function(lang){
	    			return (key2string.key2stringWithLangStrict(paramgrp.stringkey,strings,lang));
	    		};
	    });
	        
	    	defered.resolve({parameters:params, parametergroups:paramgrps});
		    }, function(rest) {
			console.log("Error loading sampleparameters");
		 });
	    return defered.promise;
	};
	
	
	this.hideSTParameter = function(parameter){
		return restfactory.PUT('update-st-parameter',{parameterid:parameter, hidden: true});
	}
	
	
	this.moveParameterToGrp = function(parameter,destination){
		return restfactory.PUT('move-parameter-to-grp',{parameterid:parameter,destination:destination});
	}
	

	
	this.updateGroupRights = function (updatedRights){
		return restfactory.PUT('update-group-rights',updatedRights);
	}
	
    
	
    this.updateParameter = function (parameter){
        return restfactory.PUT("update-st-parameter",parameter);
    };
    
    
	
    this.updateParamGrp = function (name, language, paramgrpid){
        return restfactory.PUT("update-st-paramgrp",{"newname":name, "paramgrpid":paramgrpid, "language":language});
    };



    this.updateSampleTypeData = function(sampletypeID, parameter){
        // field is a string. It is either "name" or "description".
        var tempObj = {"sampletypeid":sampletypeID,"field":parameter.field,"newvalue":parameter.data.value,"lang":parameter.lang};
        return restfactory.POST('update-sample-type-data',tempObj);
    };
    
    

	this.updateUserRights = function (updatedRights){
		return restfactory.PUT('update-user-rights',updatedRights);
	}
	
	
};


angular.module('unidaplan').service('avSampleTypeService', ['restfactory','$q','$rootScope','$translate','key2string',avSampleTypeService]);

})();