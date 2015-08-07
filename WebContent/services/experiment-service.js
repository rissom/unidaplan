(function(){
'use strict';

var experimentService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.

	var thisController=this;
	
	this.getExperiment = function(id) {
        var defered=$q.defer();
    	    	var thisController=this;
    			var promise = restfactory.GET("experiment?id="+id);
    	    	promise.then(function(rest) {
	    	    	thisController.experiment = rest.data.experiment;
	    	    	thisController.strings = rest.data.strings;
	    	    	thisController.translate();
	    	    	defered.resolve(thisController.experiment)
    	    	}, function(rest) {    	    		
    	    		console.log("Error loading experiment");
    	    		defered.reject({"error":"Error loading experiment"});
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
		return restfactory.POST("delete-process?id="+id);
	}
	
	
	
	this.translate = function() {
		this.experiment.trname=key2string.key2string(this.experiment.name,this.strings);
		angular.forEach(this.experiment.parameters, function(parameter) {
			parameter.trname=key2string.key2string(parameter.stringkeyname,thisController.strings);
		})
	}
	
}


angular.module('unidaplan').service('experimentService', ['restfactory','$q','$translate','key2string',experimentService]);

})();