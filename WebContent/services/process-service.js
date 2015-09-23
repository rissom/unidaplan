(function(){
'use strict';

var processService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.

	
	this.statusStrings = [$translate.instant("OK"),
			              $translate.instant("attention"),
			              $translate.instant("failed")];
	
	
	
	this.setStatus = function(process,status){
		return this.updateParameter(process.id,process.statuspid,status);
	}
	
	
	
	this.addProcessType = function(process){
		return restfactory.POST("add-process-type",process);
	}
	
	
	
	this.getProcess = function(id) {
        var defered=$q.defer();
    	    	var thisController=this;
    			var promise = restfactory.GET("process?id="+id);
    	    	promise.then(function(rest) {
	    	    	thisController.process = rest.data;
	    	    	thisController.strings = rest.data.strings;
	    	    	thisController.translate();
	    	    	thisController.pushProcess(thisController.process);
	    	    	defered.resolve(thisController.process)
    	    	}, function(rest) {    	    		
    	    		console.log("Error loading sampletypes");
    	    		defered.reject({"error":"Error loading sampletypes"});
    	    	});
		return defered.promise;
	}
      
	
	
	this.pushProcess = function(process){
		var i;
		var found=false;
		if (this.recentProcesses==undefined) {
			this.recentProcesses=[]
		}
		var tProcess={"id":process.id,"processtype":process.processtype,"pnumber":process.pnumber};
		for (i=0;i<this.recentProcesses.length;i++){
			if (this.recentProcesses[i].pnumber==tProcess.pnumber &&
				this.recentProcesses[i].processtype==tProcess.processtype){
				found=true			
			}
		}
		if (!found) {
			this.recentProcesses.push(tProcess);
		}
		if (this.recentProcesses.length>20){
			this.recentProcesses.slice(0,this.recentProcesses.length-20);
		}
	}
	
	
	
	// delete a process (also from recent processes)
	this.deleteProcess = function(id){
			for (var i=0;i<this.recentProcesses.length;i++){
				if (this.recentProcesses[i].id==id){
					this.recentProcesses.splice(i,1);
				}
			}
		return restfactory.DELETE("delete-process?id="+id);
	}
	
	
	
	this.updateParameter=function(processID,paramID,newValue){
		var parameter={"processid":processID,"parameterid":paramID, "value":newValue};	
		return restfactory.POST('update-process-parameter',parameter);
	}
	
	
	
	// delete a process (also from recent processes)
	this.deleteProcessType = function(id){
		return restfactory.DELETE("delete-process-type?id="+id);
	}
	
	
	
	this.translate = function() {
		
		this.process.trprocesstype = key2string.key2string(this.process.pt_string_key,this.strings)		
		var thisController=this;
		angular.forEach(thisController.process.parameters, function(parameter) {
			parameter.trname= key2string.key2string(parameter.stringkeyname,thisController.strings);
		})
	}
	
}


angular.module('unidaplan').service('processService', ['restfactory','$q','$translate','key2string',processService]);

})();