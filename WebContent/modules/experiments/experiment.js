(function(){
'use strict';

function experimentController($modal,$scope,$stateParams,experimentService,restfactory,$translate,$state,key2string,
							  avSampleTypeService,avProcessTypeService,experimentData,ptypes,stypes) {
	
	this.experiment = experimentData;
	
	this.editmode=$stateParams.editmode==='true';
	
	this.sampleActions = [$translate.instant("Go to sample"),
	                      $translate.instant("Delete Sample from Experiment"),
	                      $translate.instant("Replace sample")];	

	var thisController =this;	
	
	this.avProcesses = ptypes
	
	
	
	this.addParameter = function(){
		console.log("add-a-parameter");
	}
	
	

	this.deleteParameter = function(parameter){
		var promise = experimentService.deleteParameter(parameter.id);
		promise.then(function(){reload();});
	}
	
	
	
	this.addSample = function () {
		  var modalInstance = $modal.open({
		    animation: false,
		    templateUrl: 'modules/modal-sample-choser/modal-sample-choser.html',
		    controller: 'modalSampleChoser as mSampleChoserCtrl',
//		    size: 'sm',
		    resolve: {
		    	mode		  : function(){return 'immediate'; },
		    	samples 	  : function(){return []; },
		        types         : function(){return stypes; },
		    	except		  : function(){
		    		return thisController.experiment.samples?thisController.experiment.samples:[];
		    		
//		    		{sampleid:thisController.experiment.samples[0].sampleid,
//						    typeid:thisController.experiment.samples[0].typeid,
//						    name:thisController.experiment.samples[0].name}
		    	},
			    buttonLabel	  : function(){return ''; } 	// there is no button
			}		        
		  });
		  
		  modalInstance.result.then(function (result) {  // get the new Samplelist + Info if it is changed from Modal. 
		      if (result.changed) {
		    	  if (result.chosen.length>0){
		    		    if (thisController.experiment.samples==undefined) {
		    		    	thisController.experiment.samples=[];
		    		    }
		    			var promise=experimentService.addSampleToExperiment(thisController.experiment.id,result.chosen[0].sampleid,thisController.experiment.samples.length+1);
		    			promise.then(function(){reload();});		    	  }
		      }
		    }, function () {
		      console.log('Strange Error: Modal dismissed at: ' + new Date());
		    });
	  };
	 
	  
	  
    this.showParam=function(parameter){
	  	if (parameter.datatype===7){
	  		var date=new Date(parameter.value);
	  		return date.toLocaleDateString()+", "+date.toLocaleTimeString();  		
	  	} else {
	  		return parameter.value;
	  	} 
	}
    
    
	
	this.replaceSample = function (sample) {
		 console.log ("replacing")
		  var modalInstance = $modal.open({
		    animation: false,
		    templateUrl: 'modules/modal-sample-choser/modal-sample-choser.html',
		    controller: 'modalSampleChoser as mSampleChoserCtrl',
//		    size: 'sm',
		    resolve: {
		    	mode		  : function(){return 'immediate'; },
		    	samples 	  : function(){return []; },
		        types         : function(){return stypes; },
		    	except		  : function(){
//		    		console.log("thisSamples",sample);
		    		return [{sampleid:sample.sampleid,
						    typeid:sample.typeid,
						    name:sample.name}]
		    	},
			    buttonLabel	  : function(){return ''; } 	// there is no button
			}
		  });
		  
		  modalInstance.result.then(function (result) {  // reload the experiment with updated list. 
		      if (result.changed) {
		    	  if (result.chosen.length>0){
		    		  var promise=experimentService.replaceSampleInExperiment(sample.id,result.chosen[0].sampleid);
		    		  promise.then(function(){reload();});
		    	  }
		      }
		    }, function () {
		      console.log('Strange Error: Modal dismissed at: ' + new Date());
		    });
	  };
	  
	  
	
	this.getSampleType = function(sample) {
//		console.log ("getting Sampletype with id:", id)
		return avSampleTypeService.getType(sample,stypes);
	}
	
	
	
	this.getProcessType = function(process) {
//		console.log ("getting Sampletype with id:", id)
		return avProcessTypeService.getProcessType(process,ptypes);
	}
	
	
	
	this.getProcessRecipes = function(process){
		return avProcessTypeService.getProcessRecipes(process,ptypes);
	}
	
	
	this.setProcesstype = function(process, processtype){
		var promise= experimentService.setProcesstype(process.id, processtype.id);
		promise.then(function(){reload();});
	}
	
	
	this.changeRecipe = function(pprocess){
		var promise=experimentService.ExpStepChangeRecipe(pprocess.process_step_id,pprocess.recipe);
		promise.then(function(){reload();});
	}
	
	
	
	this.getWidth = function(){
		//adjusts the width of a div for the experiment in edit-mode. 
		// So big tables are not squeezed
		var numProc=0;
		if (this.experiment.processes) { 
			numProc=this.experiment.processes.length; 
		}
		var mystyle= {'width':500+230*numProc+'px'};
		return mystyle;
	}
	
	
	
	this.changeProcessStep = function($event,process,sample){
			// unchecking a process step
			if (!$event.target.checked) {
				var p=this.getPlannedProcess(process,sample.pprocesses)
				var promise = experimentService.deactivateProcessStep(p.process_step_id);
				promise.then(function(){reload();});
			} else{
				var promise = experimentService.addProcessStep(process.id, sample.id);
				promise.then(function(){reload();});
			}
	}
	
	
	
	this.addProcessToExperiment = function(processtype){
		// add a process (not a single step) to the experiment
		var promise= experimentService.addProcessToExperiment(processtype.id, this.experiment.id);
		promise.then(function(){reload();});
	}
	
	
	
	this.moveProcessRight = function(process){
		for (var i=0; i<this.experiment.processes.length; i++){
			if (this.experiment.processes[i].position==process.position+1){
				this.experiment.processes[i].position=this.experiment.processes[i].position-1;
			}
		}
		process.position=process.position+1;
		this.updatePositionsForProcesses();
	}
	
	
	
	this.moveProcessLeft = function(process){
		for (var i=0; i<this.experiment.processes.length; i++){
			if (this.experiment.processes[i].position==process.position-1){
				this.experiment.processes[i].position=this.experiment.processes[i].position+1;
			}
		}
		process.position=process.position-1;
		this.updatePositionsForProcesses();
	}
	
	
	
	this.markColumn=function(processID){
		var promise=experimentService.markAllProcesses(this.experiment.id,processID);
		promise.then(function(){reload();});
	}
	
	
	
	this.updatePositionsForProcesses = function(){
		experimentService.updatePositionsForProcessesInExperiment(this.experiment.processes);
	}
	
	
	
	this.deleteProcess = function(process){
		var promise = experimentService.deleteProcess(process.id);
		promise.then(function(){reload();});
	}
	
	
	
	this.getPlannedProcess = function(process,pprocesses){
		var retprocess=false
		if (pprocesses){
			for (var i=0; i<pprocesses.length;i++){
				if (pprocesses[i].eppprocess==process.id){
					retprocess=pprocesses[i];
				}
			}
		}
		return retprocess;
	}
	
	
	
//	var thisController=this;
	angular.forEach (experimentData.samples, function(sample){
		// Arranging of planned and finished experiments. Finished Experiments are sorted by time. 
		// Planned Experiments are matched to finished Experiments.
		var mprocesses=[];
		var pplength=0;
		var finishedProcesses=[];
		var plannedProcesses=[];
		if (sample.fprocesses!=undefined) {
			for (var i=0;i++;i<sample.fprocesses.length){
				fprocess[i].date=Date(fprocesses[i].date);
			}
			finishedProcesses=sample.fprocesses.sort(function(a,b){return b.date<a.date}) // Sort finished processes by date
		}
		if (sample.pprocesses!=undefined){
			plannedProcesses=sample.pprocesses;
		}
		var fpi=0;
		var ppi=0;
		while (fpi<finishedProcesses.length || ppi<plannedProcesses.length) {
			var fp={};
			var pp={};
			if (fpi<finishedProcesses.length) {
				fp=finishedProcesses[fpi];
				if (ppi<plannedProcesses.length)
					if (finishedProcesses[fpi].processtype == plannedProcesses[ppi].processtype){
						pp=plannedProcesses[ppi++]; 
					}
				fpi=fpi+1;
			}else{
				pp=plannedProcesses[ppi++]; 
			}	
			mprocesses.push({"fprocess":fp,"pprocess":pp});
		}
		sample.mprocesses=mprocesses;
	})
		
			
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			parameter.editing=false; 
			var oldValue=parameter.value;
			parameter.value=newValue;
			parameter.experimentid=this.experiment.id;
			var res = restfactory.POST('update-experiment-parameter',parameter);
			res.then(function(data, status, headers, config) {
				 },
				 function(data, status, headers, config) {
					parameter.value=oldValue;
					console.log('error');
					console.log(data);
				 }
				);
		}
		if (keyCode===27) {		// Escape key pressed
			parameter.editing=false;		
		}
	}
	
	
	
	this.commentKeyUp = function(keyCode,newValue,sample) {
		if (keyCode===13) {				// Return key pressed
			sample.editing=false; 
			var oldValue=sample.trnote;
			sample.trnote=newValue;
			// save new comment in database.
			var res = experimentService.updateExperimentSampleComment(sample.id,newValue);
				res.then(function(data) {
						 },
						 function(data) {
							sample.trnote=oldValue;
							console.log('error');
							console.log(data);
						 }
						);
		}
		if (keyCode===27) {		// Escape key pressed
			sample.editing=false;		
		}
	}
	
	
	
	this.commentKeyUp2 = function(keyCode,newValue,pprocesses,process) {
		var step=this.getPlannedProcess(process,pprocesses);
		if (keyCode===13) {				// Return key pressed
			step.edit=false; 
			var oldValue=step.trnote;
			step.trnote=newValue;
			// save new comment in database.
			var promise = experimentService.updateExperimentStepComment(step.process_step_id,newValue);
			promise.then(function(data) {
						 },
						 function(data) {
							step.trnote=oldValue;
							console.log('error');
							console.log(data);
						 }
						);
		}
		if (keyCode===27) {		// Escape key pressed
			step.edit=false;		
		}
	}
	
	
	
	var reload=function() {
	    var current = $state.current;
	    var params = angular.copy($stateParams);
	    params.editmode=thisController.editmode;
	    return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	}
	
	
	
	this.deleteSample = function(sample){
		var promise= experimentService.deleteSampleFromExperiment(sample.id);
		promise.then(function(){reload();});
	}

	

}
    
        
angular.module('unidaplan').controller('experimentController',['$modal','$scope','$stateParams','experimentService','restfactory','$translate','$state','key2string','avSampleTypeService',
               'avProcessTypeService','experimentData','ptypes','stypes',experimentController]);

})();