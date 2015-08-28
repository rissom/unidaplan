(function(){
'use strict';

function experimentController($scope,$stateParams,experimentService,restfactory,$translate,$state,key2string,avSampleTypeService,avProcessTypeService,experimentData,ptypes,stypes) {
	
	this.experiment = experimentData;

	this.sampleActions = [$translate.instant("Go to sample"),$translate.instant("Delete Sample from Experiment"),$translate.instant("Replace sample")];
	
	this.getSampleType = function(sample) {
//		console.log ("getting Sampletype with id:", id)
		return avSampleTypeService.getType(sample,stypes);
	}
	
	this.getProcessType = function(process) {
//		console.log ("getting Sampletype with id:", id)
		return avProcessTypeService.getProcessType(process,ptypes);
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
	    return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	}
	
	this.action = function(index,sample){
		if (index==0){
			$state.go("sample",{sampleID:sample.sample})
		}
		if (index==1){
			var promise= experimentService.deleteSampleFromExperiment(sample.id);
			promise.then(function(){reload();});
		}
		if (index==2){
			console.log("Replace Sample");
			var sample2=1;
			var promise=experimentService.replaceSampleInExperiment(sample.id,sample2);
			promise.then(function(){reload();});

		}
	}
	
};
    
        
angular.module('unidaplan').controller('experimentController',['$scope','$stateParams','experimentService','restfactory','$translate','$state','key2string','avSampleTypeService',
               'avProcessTypeService','experimentData','ptypes','stypes',experimentController]);

})();