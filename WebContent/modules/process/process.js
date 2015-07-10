(function(){
  'use strict';

function process(restfactory,$scope,$translate){
  
  this.process={trprocesstype:""}; 
  
  
  this.loadProcess = function(id){
  		var thisProcessController=this;
		var promise = restfactory.GET("process.json?id="+id);
	    promise.then(function(rest) {
	    	thisProcessController.process = rest.data;
	    	thisProcessController.translate($translate.use());
	    }, function(rest) {
	    	console.log("Error loading process");
	    });
  }; 
  
  
  $scope.$on('language changed', function(event, args) {
		$scope.processCtrl.translate(args.language);
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

	
};

angular.module('unidaplan').controller('process', ['restfactory', '$scope', '$translate', process]);

})();