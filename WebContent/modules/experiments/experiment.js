(function(){
'use strict';

function experimentController($modal,$scope,$stateParams,experimentService,restfactory,$translate,$state,key2string,
							  avSampleTypeService,avProcessTypeService,experimentData,ptypes,stypes) {
	
	this.experiment = experimentData;
	
	this.editmode=$stateParams.editmode;
	
	this.sampleActions = [$translate.instant("Go to sample"),$translate.instant("Delete Sample from Experiment"),$translate.instant("Replace sample")];	

	var thisController =this;	
	
	this.avProcesses = ptypes
	
	
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
		    		console.log("thisSamples",thisController.experiment.samples);
		    		return {sampleid:thisController.experiment.samples[0].sample,
						    typeid:thisController.experiment.samples[0].typeid,
						    name:thisController.experiment.samples[0].name}
		    	},
			    buttonLabel	  : function(){return ''; } 	// there is no button
			}		        
		  });
		  
		  modalInstance.result.then(function (result) {  // get the new Samplelist + Info if it is changed from Modal. 
		      if (result.changed) {
		    	  if (result.chosen.length>0){
		    		   console.log("result",result.chosen);
		    			var promise=experimentService.addSampleToExperiment(thisController.experiment.id,result.chosen[0].sampleid,thisController.experiment.samples.length+1);
		    			promise.then(function(){reload();});		    	  }
		      }
		    }, function () {
		      console.log('Strange Error: Modal dismissed at: ' + new Date());
		    });
	  };
	  
//	  
//	this.addSingleSample=function(sample){
//		// contact the server
//
//	};
	
		
		this.replaceSample = function(sample){
			var sample2=1;
			
			promise.then(function(){reload();});
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
		    		console.log("thisSamples",sample);
		    		return {sampleid:sample.sample,
						    typeid:sample.typeid,
						    name:sample.name}
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
	
	
	
	this.setProcesstype = function(process, processtype){
		var promise= experimentService.setProcesstype(process.id, processtype.id);
		promise.then(function(){reload();});
	}
	
	
	
	this.addProcessToExperiment = function(processtype){
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
		console.log ("noch alles gut.")
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
		console.log("Marking all")
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
		var mprocesses=[];
		var fplength=0;
		var pplength=0;
		if (sample.fprocesses!=undefined) {
			fplength=sample.fprocesses.length
		}
		if (sample.pprocesses!=undefined){
			pplength=sample.pprocesses.length;
		}
		var j = Math.max (fplength,pplength)
			for (var i=0;i<j;i++){
				var fp={}
				if (sample.fprocesses!=undefined && i<sample.fprocesses.length) {
					fp=sample.fprocesses[i]
				}
				var pp={};
				if (sample.pprocesses!=undefined && i<sample.pprocesses.length) {
					pp=sample.pprocesses[i]
				}
				mprocesses.push({"fprocess":fp,"pprocess":pp})
			}
		sample.mprocesses=mprocesses;
	})
		
			
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			parameter.editing=false; 
			var oldValue=parameter.value;
			parameter.value=newValue;
			 if (parameter.pid) {
				var res = restfactory.POST('update-experiment-parameter.json',parameter);
				res.then(function(data, status, headers, config) {
						 },
						 function(data, status, headers, config) {
							parameter.value=oldValue;
							console.log('verkackt');
							console.log(data);
						 }
						);
			 } else {
				var res = restfactory.POST('add-experiment-parameter.json?sampleid='+this.experiment.id,parameter);
					res.then(function(data, status, headers, config) {
							 },
							 function(data, status, headers, config) {
								parameter.value=oldValue;
								console.log('verkackt');
								console.log(data);
							 }
							);
			 }
		}
		if (keyCode===27) {		// Escape key pressed
			parameter.editing=false;		
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

	

};
    
        
angular.module('unidaplan').controller('experimentController',['$modal','$scope','$stateParams','experimentService','restfactory','$translate','$state','key2string','avSampleTypeService',
               'avProcessTypeService','experimentData','ptypes','stypes',experimentController]);

})();