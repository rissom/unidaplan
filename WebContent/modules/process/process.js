(function(){
  'use strict';

function process(restfactory,sampleService,avSampletypeService,$modal,$scope,$state,$stateParams,$translate){
  
  var thisController=this;
  
  this.chosenSamples=[];
	
  this.process={trprocesstype:""}; 
  
  this.showtypes=function(){
	  return sampleService.types
  }
  
  this.loadProcess = function(){
  		var thisProcessController=this;
		var promise = restfactory.GET("process.json?id="+$stateParams.processID);
	    promise.then(function(rest) {
	    	thisProcessController.process = rest.data;
	    	thisProcessController.translate($translate.use());
	    }, function(rest) {
	    	console.log("Error loading process");
	    });
  };
  
 
  
  this.openDialog = function () {
	  var modalInstance = $modal.open({
	    animation: false,
	    templateUrl: 'modules/process/modal-sample-choser.html',
	    controller: 'modalSampleChoser as mSampleChoserCtrl',
	    size: 'lg',
	    resolve: {
	    	chosenSamples: function () {
	        return thisController.chosenSamples;
	      }
	    }
	  });
	  
	  
	  modalInstance.result.then(function (myChosenSamples) {
	      thisController.chosenSamples = myChosenSamples;
	    }, function () {
	      console.log('Modal dismissed at: ' + new Date());
	    });
  };
  
  
  $scope.$on('language changed', function(event, args) {
		$scope.processCtrl.translate(args.language);
		sampleService.translate();
	});

	
  this.translate = function(lang) {
	var strings=this.process.strings;
	var parameters=this.process.parameters;
	var thisProcess=this.process
	var notyetfound=true;
	
	angular.forEach(strings, function(translation) {		
		if (notyetfound==true && thisProcess.pt_string_key==translation.string_key){
			thisProcess.trprocesstype = translation.value;				
				if (translation.language==lang){
					notyetfound=false;
				}
		}
	})
	
		
	angular.forEach(parameters, function(parameter) {  // Find all the parameter names.
		notyetfound=true;
		angular.forEach(strings, function(translation) {
			if (notyetfound==true && parameter.stringkeyname==translation.string_key){
				parameter.trname=translation.value;				
					if (translation.language==lang){
						notyetfound=false;
					}
			}
		})
	})	
  };

  //activate function
  this.loadProcess();
//  sampleService.loadTypes();
};

angular.module('unidaplan').controller('process', ['restfactory', 'sampleService', 'avSampletypeService', '$modal', '$scope', '$state', '$stateParams', '$translate', process]);

})();