(function(){
  'use strict';

function process($state,$stateParams,avSampleTypeService,types,$modal,processData,restfactory,processService){
  
  var thisController=this;
  
  this.deletable=true;
  	
  this.process=processData;
 
  this.statusStrings=processService.statusStrings;
  
  this.openDialog = function () {
	  
	  var modalInstance = $modal.open({
	    animation: false,
	    templateUrl: 'modules/modal-sample-choser/modal-sample-choser.html',
	    controller: 'modalSampleChoser as mSampleChoserCtrl',
	    size: 'lg',
	    resolve: {
	    	samples 	  : function() {
	    					return thisController.process.samples; },
	        types         : function() {
	        				return types; },
	    	except		  : function() {
		        				return [];
		        			},
		    buttonLabel	  : function() { 
		        				return 'assign to process';
		        			},
		    mode		  : function() {
		    					return 'multiple';
		    				}
		    }		        
	  });
	  
	  modalInstance.result.then(function (result) {  // get the new Samplelist + Info if it is changed from Modal. 
	      thisController.process.samples = result.chosen;
	      if (result.changed) {
	    	  thisController.assign();
	      }
	    }, function () {
	      console.log('Strange Error: Modal dismissed at: ' + new Date());
	    });
  };

  
  
  // return the translated name string of a type for a sample
  this.getType=function(sample){
	  return avSampleTypeService.getType(sample,types);
  }
  
  
  
  this.showParam=function(parameter){
  	if (parameter.datatype===7){
  		var date=new Date(parameter.value);
  		return date.toLocaleDateString()+", "+date.toLocaleTimeString().substring(0,5);  		
  	} else {
  		return parameter.value;
  	} 
  }
  
  
  
  this.status=function(){
	  switch (this.process.status){
		  case 3 : return this.statusStrings[2]; break;
		  case 2 : return this.statusStrings[1]; break;
		  default: return this.statusStrings[0]; 
	  }
  }
  
  
  
  this.setStatus=function(){
	  var promise=processService.setStatus(processData,this.newStatus);
	  promise.then(function(){
		  reload();});
  }
  
  
  
  
  this.paramKeyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			parameter.editing=false; 
			var oldValue=parameter.value;
			parameter.value=newValue;
			var res = processService.updateParameter(this.process.id,parameter.id,newValue);
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
  
  
  
  this.deleteProcess = function(){
	  return processService.deleteProcess(this.process.id);
  }
  
  
  
  this.assign=function(){
	  var samples2assign={samples:this.process.samples, id:processData.id};
	  var promise = restfactory.POST("add-sample-to-process",samples2assign);
  }
 
  
  
  var reload=function() {
  	var current = $state.current;
  	var params = angular.copy($stateParams);
  	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
  }
  
};

angular.module('unidaplan').controller('process', ['$state','$stateParams','avSampleTypeService','types', '$modal',
                                                   'processData','restfactory','processService',process]);

})();