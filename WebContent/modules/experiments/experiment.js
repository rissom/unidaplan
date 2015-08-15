(function(){
'use strict';

function experimentController(restfactory,avSampleTypeService,avProcessTypeService,experimentData,ptypes,stypes) {
	
	this.experiment =  experimentData;
	
	this.getSampleType = function(sample) {
//		console.log ("getting Sampletype with id:", id)
		return avSampleTypeService.getType(sample,stypes);
	}
	
	
	this.getProcessType = function(process) {
//		console.log ("getting Sampletype with id:", id)
		return avProcessTypeService.getProcessType(process,ptypes);
	}
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			console.log ("fertisch!")
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
	
	
};
    
        
angular.module('unidaplan').controller('experimentController',['restfactory','avSampleTypeService','avProcessTypeService','experimentData','ptypes','stypes',experimentController]);

})();