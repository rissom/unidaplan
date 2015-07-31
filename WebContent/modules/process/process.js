(function(){
  'use strict';

function process(avSampleTypeService,types,$modal,processData,restfactory){
  
  var thisController=this;
  	
  this.process=processData;

  
  this.openDialog = function () {
	  
	  var modalInstance = $modal.open({
	    animation: false,
	    templateUrl: 'modules/process/modal-sample-choser.html',
	    controller: 'modalSampleChoser as mSampleChoserCtrl',
	    size: 'lg',
	    resolve: {
	    	samples 	  : function() {
	    					return thisController.process.samples; },
	        types         : function() {
	        				return types; }
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
  
  
  this.assign=function(){
	  var samples2assign={samples:this.process.samples, id:processData.id};
	  var promise = restfactory.POST("add-sample-to-process",samples2assign);
  }
 
  
};

angular.module('unidaplan').controller('process', ['avSampleTypeService','types', '$modal', 'processData','restfactory',process]);

})();