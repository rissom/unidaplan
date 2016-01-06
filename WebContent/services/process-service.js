(function(){
'use strict';

var processService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.

	var thisController=this;

	
	
	this.setStatus = function(process,status){
		return this.saveParameter(process.id,{id:process.statuspid,value:status});
	};
	
	
	
	this.addProcessType = function(process){
		return restfactory.POST("add-process-type",process);
	};
	
	
	
	this.getProcess = function(id) {
        var defered=$q.defer();
		var promise = restfactory.GET("process?id="+id);
    	promise.then(function(rest) {
    	thisController.process = rest.data;
	    	    	
	    	// add translation functions
	    	var strings = rest.data.strings;
	    	thisController.process.fprocesstype = function(){
	    		return key2string.key2string(thisController.process.pt_string_key,strings);
	    	};
	    	thisController.process.trprocesstype = key2string.key2string(thisController.process.pt_string_key,strings);
			angular.forEach(thisController.process.parametergroups, function(paramgrp) {
				paramgrp.grpnamef=function(){
					return key2string.key2string(paramgrp.paramgrpkey,strings);
				};
				angular.forEach(paramgrp.parameter, function(parameter) {
					parameter.namef=function(){
						return key2string.key2string(parameter.stringkeyname,strings);
					};
					if (parameter.parametergroup){
						parameter.grpnamef=function(){
							return key2string.key2string(parameter.parametergrp_key,strings);
						};
					}
					if (parameter.unit){
						parameter.unitf=function(){
							return key2string.key2string(parameter.unit,strings); 
						};
					}
					if (parameter.datatype==="date" || parameter.datatype==="timestamp"){			
						parameter.newDate=new Date(parameter.value);
					}
				});
			});   	
			thisController.pushProcess(thisController.process);
	    	defered.resolve(thisController.process);
    	}, function(rest) {    	    		
    		console.log("Error loading process");
    		defered.reject({"error":"Error loading process"});
    	});
		return defered.promise;
	};
      
	
	
	this.pushProcess = function(process){
		var i;
		var found=false;
		if (this.recentProcesses===undefined) {
			this.recentProcesses=[];
		}
		var tProcess={"id":process.id,"processtype":process.processtype,"pnumber":process.pnumber};
		for (i=0;i<this.recentProcesses.length;i++){
			if (this.recentProcesses[i].pnumber==tProcess.pnumber &&
				this.recentProcesses[i].processtype==tProcess.processtype){
				found=true;	
			}
		}
		if (!found) {
			this.recentProcesses.push(tProcess);
		}
		if (this.recentProcesses.length>20){
			this.recentProcesses.slice(0,this.recentProcesses.length-20);
		}
	};
	
	
	
	// delete a process (also from recent processes)
	this.deleteProcess = function(id){
			for (var i=0;i<this.recentProcesses.length;i++){
				if (this.recentProcesses[i].id==id){
					this.recentProcesses.splice(i,1);
				}
			}
		return restfactory.DELETE("delete-process?id="+id);
	};
	

	
	this.saveParameter = function(processid,parameter) {
		var json={processid:processid, parameterid:parameter.id, value:parameter.value};
		if ("date" in parameter) {
			json.date=parameter.date;
			json.tz=parameter.tz;
		}
		if ("time" in parameter) {
			json.time=parameter.time;
		}
		if ("error" in parameter) {
			json.error=parameter.error;
		} 
		return restfactory.POST('save-process-parameter',json);
	};

	
	
	
	// delete a processtype
	this.deleteProcessType = function(id){
		return restfactory.DELETE("delete-process-type?id="+id);
	};
	
	
	
	// duplicate a process
	this.duplicateProcessType = function(id){
		return restfactory.POST("duplicate-process-type?id="+id);
	};
	
	
	
	this.translate = function() {
	};
	
};


angular.module('unidaplan').service('processService', ['restfactory','$q','$translate','key2string',processService]);

})();