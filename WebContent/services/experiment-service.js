(function(){
'use strict';

var experimentService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.

	var thisController=this;
	
	this.statusItems = ['doof','bloed','schwachsinn']
	
	this.addExperiment = function(){
		return restfactory.POST("add-experiment",{name:{"de":"neues Experiment", "en":"new Experiment"}});
	};
	
	
	
	this.addParameters = function(experimentid,parameters) {
		return  restfactory.POST("add-experiment-parameter",{ experimentid : experimentid , parameters : parameters });
	};
	
	
	
	this.addSampleToExperiment = function (expid,sampleid,pos){
		return  restfactory.POST("add-sample-to-experiment?experimentid="+expid+"&position="+pos+"&sampleid="+sampleid);
	};

	
	
	this.addProcessToExperiment = function(processType,experimentID){
		return restfactory.POST("add-process-to-experiment?processtype="+processType+"&experimentid="+experimentID);
	};
	
	
	
	// delete an attached file
	this.deleteFile = function(fileID){
		return restfactory.DELETE("delete-file?fileid="+fileID)
	}
	
	
	
	this.ExpStepChangeRecipe = function(id,recipe) {
		return restfactory.POST("exp-step-change-recipe?processstepid="+id+"&recipe="+recipe);
	};
	
	
	
	// delete an experiment (also from recent experiments)
	this.deleteExperiment = function(id){
			if (this.recentExperiments!==undefined) {
				for (var i=0;i<this.recentExperiments.length;i++){
					if (this.recentExperiments[i].id==id){
						this.recentExperiments.splice(i,1);
					}
				}
			}
		return restfactory.DELETE("delete-experiment?id="+id);
	};
	
	
	

	
	this.getExperiment = function(id) {
        var defered=$q.defer();
			var promise = restfactory.GET("experiment?id="+id);
	    	promise.then(function(rest) {
    	    	var experiment = rest.data.experiment;
    	    	var strings = rest.data.strings;
    			experiment.namef = function(){
    				return key2string.key2string(experiment.name,strings);
    			};
    			experiment.nameLang = function(lang){
    				return key2string.key2stringWithLangStrict(experiment.name,strings,lang);
    			};
    			angular.forEach(experiment.parameters, function(parameter) {
    				parameter.namef=function(){
    					return key2string.key2string(parameter.stringkeyname,strings);
    				};
    				parameter.nameLang=function(lang){				
    					return key2string.key2stringWithLangStrict(parameter.stringkeyname,strings,lang);
    				};
     				parameter.unitf=function(){
    					return key2string.unitf(parameter.unit,strings);
    				};
    				parameter.unitLang=function(lang){				
    					return key2string.key2stringWithLangStrict(parameter.unit,strings,lang);
    				};
    				if (parameter.datatype==="date") {
    					parameter.newDate=new Date(parameter.value);
    					parameter.date=new Date(parameter.value);
    				}
    			});
    			angular.forEach(experiment.samples, function(sample){
    				if (sample.note!==undefined) {
    					sample.trnote=key2string.key2string(sample.note,strings);
    				}
    				angular.forEach(sample.pprocesses, function(pprocess){
    					if (pprocess.note){
    						pprocess.trnote=key2string.key2string(pprocess.note,strings);
    					}
    				});
    			});
    			thisController.pushExperiment(experiment);
    	    	defered.resolve(experiment);
	    	}, function(rest) {    	    		
	    		console.log("Error loading experiment");
	    		defered.reject({"error":"Error loading experiment"});
	    	});
		return defered.promise;
	};
	
	
	
	this.getExperiments = function() {
        var defered = $q.defer();
		var promise = restfactory.GET("experiments");
	    promise.then(function(rest) {
	    	thisController.experiments = rest.data.experiments;
	    	thisController.expsStrings = rest.data.strings;
			angular.forEach(thisController.experiments, function(anExp) {
				anExp.namef = function(){
					return key2string.key2string(anExp.name,thisController.expsStrings);
				};
			});
	    	defered.resolve(thisController.experiments);
	    }, function(rest) {
	    });
		return defered.promise;
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
	
	
	
	// save experiment for "recent experiments"
	this.pushExperiment = function(experiment){
		var found = false;
		if (this.recentExperiments === undefined) {
			this.recentExperiments=[];
		}
		var tExperiment = {"id":experiment.id,"number":experiment.number,"namef":experiment.namef,"name":experiment.name};
		for (var i = 0; (i < this.recentExperiments.length && !found); i++){
			if (this.recentExperiments[i].id == tExperiment.id) {
				found = true;	
			}
		}
		if (!found) {
			this.recentExperiments.push(tExperiment);
		}
		if (this.recentExperiments.length > 20){
			this.recentExperiments.slice(0,this.recentExperiments.length - 20);
		}
	};
	
	
	
	this.updateGroupRights = function (updatedRights){
		return restfactory.PUT('update-group-rights',updatedRights);
	}
	
	
	
	this.updateUserRights = function (updatedRights){
		return restfactory.PUT('update-user-rights',updatedRights);
	}
	
	
	
	this.updatePositionsForProcessesInExperiment=function(processes){
		return restfactory.POST("update-positions-for-processes-in-experiment",processes);
	};
	
	
	
	this.updateExperimentName = function(id,language,newName){
		var json = {"id":id, "language":language, "name":newName};
		return restfactory.PUT('update-experiment-name',json);
	};
	
	
	
	this.updateExperimentParameter=function(parameter,experimentid){
		
		var json={experimentid:experimentid, parameterid:parameter.id, value:parameter.value};
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
		
		return restfactory.POST('update-experiment-parameter',parameter);
	};
	
	
	
	this.updateExperimentSampleComment = function(id,comment){
		var promise = restfactory.POST('update-experiment-sample-comment',
				{"id":id,"comment":comment});
		return promise;
	};
	
	

	this.updateExperimentStepComment = function(id,comment){
		var promise = restfactory.POST('update-experiment-step-comment',
						{"id":id,"comment":comment});
		return promise;
	};
	
	
	
	this.updateNumber = function(id,newNumber){
		var promise = restfactory.PUT('update-experiment-number?id='+id+'&number='+newNumber);
		return promise;
	};
	
	
	
	this.deleteSampleFromExperiment = function(id){
		// Removes a sample from an experiment. 
		return restfactory.DELETE("delete-sample-from-experiment?id="+id);
	};
	
	
	
	this.deleteParameter=function(id){
		//removes a proces from an experiment
		return restfactory.DELETE("delete-experiment-parameter?id="+id);
	};
	
	
	
	this.deleteProcess=function(id){
		//removes a proces from an experiment
		return restfactory.DELETE("delete-process-from-experiment?id="+id);
	};
	
	
	
	this.replaceSampleInExperiment = function(id, newSampleId){
		// replaces a sample in an experiment
		return restfactory.POST("replace-sample-in-experiment?id="+id+"&sampleid="+newSampleId);
	};
	
	
};


angular.module('unidaplan').service('experimentService', ['restfactory','$q','$translate','key2string',experimentService]);

})();