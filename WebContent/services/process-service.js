(function(){
'use strict';

var processService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.

	var thisController=this;

	
	
	this.setStatus = function(process,status){
		return this.saveParameter(process.id,{id:process.statuspid,value:status});
	};
	
	
	
	this.setNumber = function(processid,number){
		return restfactory.PUT("update-process-number",{number:number, processid:processid});
	};
	
	
	
	this.addProcessType = function(process){
		return restfactory.POST("add-process-type",process);
	};
	

	
	// delete an attached file
	this.deleteFile = function(fileID){
		return restfactory.DELETE("delete-file?fileid="+fileID)
	}

	
	
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
			angular.forEach(thisController.process.fields, function(field) {
				field.namef = function(){
					return key2string.key2string(field.stringkeyname,strings);
				};
				if (field.description){
					field.descriptionf = function(){
						return key2string.key2string(field.description,strings);
					};
				};
				if (field.unit){
					field.unitf = function(){
						return key2string.key2string(field.unit,strings); 
					};
				};
			});
			thisController.pushProcess(thisController.process);
	    	defered.resolve(thisController.process);
    	}, function(rest) {    	    		
    		console.log("Error loading process");
    		defered.reject({"error":"Error loading process"});
    	});
		return defered.promise;
	};
      
	
	
	this.getProcessTypeParameters = function (processTypeID){
    	var defered=$q.defer();
		var promise = restfactory.GET('/all-process-type-params?processtypeid='+processTypeID);
		promise.then(function(rest) {			// getting the parameters
			var parameters = {parameters:rest.data.parameters,parametergrps:rest.data.parametergrps};
			var strings = rest.data.strings;
			angular.forEach(parameters.parameters,function(parameter){
				parameter.namef = function(){
					return key2string.key2string(parameter.name,strings);
				}				
			});
			angular.forEach(parameters.parametergrps,function(grp){
				grp.namef = function(){
					return key2string.key2string(grp.stringkey,strings);
				}
			});
			defered.resolve(parameters);
		}, function () {
			console.log("Error getting Parameters")
		}); // promise.then
	    return defered.promise;
	}
	
	
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
	
	

  	this.savePOParameter = function(parameter) {
  		console.log ("parameter:",parameter)
  		var json = {parameterid:parameter.parameterid, opid:parameter.opid, value:parameter.value};
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
  		return restfactory.POST("save-po-parameter",json);
  	}
  	
	
  	
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