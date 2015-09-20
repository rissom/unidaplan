(function(){
'use strict';

var experimentService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.

	var thisController=this;
	
	
	this.getExperiments = function() {
        var defered=$q.defer();
		var promise = restfactory.GET("experiments.json");
		 		
	    promise.then(function(rest) {
	    	thisController.experiments = rest.data.experiments;
	    	thisController.expsStrings = rest.data.strings;
	    	thisController.translateExps();
	    	defered.resolve(thisController.experiments)
	    }, function(rest) {
	    });
		return defered.promise;
	};
	
	
	
	this.getExperiment = function(id) {
        var defered=$q.defer();
	    	var thisController=this;
			var promise = restfactory.GET("experiment?id="+id);
	    	promise.then(function(rest) {
    	    	thisController.experiment = rest.data.experiment;
    	    	thisController.strings = rest.data.strings;
    	    	thisController.translate();
    		    thisController.pushExperiment(thisController.experiment);
    	    	defered.resolve(thisController.experiment)
	    	}, function(rest) {    	    		
	    		console.log("Error loading experiment");
	    		defered.reject({"error":"Error loading experiment"});
	    	});
		return defered.promise;
	}
	
	
	
	this.ExpStepChangeRecipe = function(id,recipe) {
		return restfactory.POST("exp-step-change-recipe?processstepid="+id+"&recipe="+recipe);
	}
	
    
	
	// save experiment for "recent experiments"
	this.pushExperiment = function(experiment){
		var found=false;
		if (this.recentExperiments==undefined) {
			this.recentExperiments=[]
		}
		var tExperiment={"id":experiment.id,"number":experiment.number,"trname":experiment.trname,"name":experiment.name};
		for (var i=0;i<this.recentExperiments.length;i++){
			if (this.recentExperiments[i].id==tExperiment.id) {
				found=true			
			}
		}
		if (!found) {
			this.recentExperiments.push(tExperiment);
		}
		if (this.recentExperiments.length>20){
			this.recentExperiments.slice(0,this.recentExperiments.length-20);
		}
	}
	
	
	
	// delete an experiment (also from recent experiments)
	this.deleteExperiment = function(id){
			if (this.recentExperiments!=undefined) {
				for (var i=0;i<this.recentExperiments.length;i++){
					if (this.recentExperiments[i].id==id){
						this.recentExperiments.splice(i,1);
					}
				}
			}
		return restfactory.DELETE("delete-experiment?id="+id);
	}
	
	
	
	this.addSampleToExperiment = function (expid,sampleid,pos){
		return  restfactory.POST("add-sample-to-experiment?experimentid="+expid+"&position="+pos+"&sampleid="+sampleid);
	}

	
	
	this.addProcessToExperiment = function(processType,experimentID){
		return restfactory.POST("add-process-to-experiment?processtype="+processType+"&experimentid="+experimentID);
	};
	
	
	
	this.markAllProcesses = function(experimentID,processID){
		return restfactory.POST("mark-all-processes-in-experiment?experimentid="+experimentID+"&processid="+processID);
	};
	
	
	
	this.deactivateProcessStep = function(processStepID){
		return restfactory.POST("deactivate-process-step?processstepid="+processStepID);
	};							

	
	
	this.addProcessStep = function(processid, sampleid){
		return restfactory.POST("add-process-step?pprocess="+processid+"&expsample="+sampleid); 
	};
	
	
	
	this.updatePositionsForProcessesInExperiment=function(processes){
		return restfactory.POST("update-positions-for-processes-in-experiment",processes);
	}
	
	
	
	this.updateExperimentSampleComment = function(id,comment){
		var promise = restfactory.POST('update-experiment-sample-comment',
				{"id":id,"comment":comment});
		return promise;
	}
	
	

	this.updateExperimentStepComment = function(id,comment){
		var promise = restfactory.POST('update-experiment-step-comment',
						{"id":id,"comment":comment});
		return promise;
	}
	
	
	
	this.deleteSampleFromExperiment = function(id){
		// Removes a sample from an experiment. 
		return restfactory.DELETE("delete-sample-from-experiment?id="+id);
	}
	
	
	
	this.deleteParameter=function(id){
		//removes a proces from an experiment
		return restfactory.DELETE("delete-experiment-parameter?id="+id);
	}
	
	
	
	this.deleteProcess=function(id){
		//removes a proces from an experiment
		return restfactory.DELETE("delete-process-from-experiment?id="+id);
	}
	
	
	
	this.replaceSampleInExperiment = function(id, newSampleId){
		// replaces a sample in an experiment
		return restfactory.POST("replace-sample-in-experiment?id="+id+"&sampleid="+newSampleId);
	}
	
	
	
	this.translate = function() {
		this.experiment.trname=key2string.key2string(this.experiment.name,this.strings);
		angular.forEach(this.experiment.parameters, function(parameter) {
			parameter.trname=key2string.key2string(parameter.stringkeyname,thisController.strings);
		})
//		if (this.recentExperiments!=undefined){
//			angular.forEach(this.recentExperiments, function(experiment) {
//				experiment.trname=key2string.key2string(experiment.name,thisController.strings);
//			})
//		}
		angular.forEach(this.experiment.samples, function(sample){
			if (sample.note!=undefined) {
				sample.trnote=key2string.key2string(sample.note,thisController.strings);
			}
			angular.forEach(sample.pprocesses, function(pprocess){
				if (pprocess.note){
					pprocess.trnote=key2string.key2string(pprocess.note,thisController.strings);
				}
			})
		});
	}
	
	
	
	this.translateExps = function() {
		angular.forEach(this.experiments, function(anExp) {
			anExp.trname=key2string.key2string(anExp.name,thisController.expsStrings)
		})
	};
	
}


angular.module('unidaplan').service('experimentService', ['restfactory','$q','$translate','key2string',experimentService]);

})();