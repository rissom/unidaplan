(function(){
  'use strict';

function process(restfactory){
  
  this.process={}; 
  
 
  this.loadProcess = function(id){
	  		var thisProcessController=this;
			var promise = restfactory.GET("process.json?id="+id);
		    promise.then(function(rest) {
		    	thisProcessController.process = rest.data;
		    }, function(rest) {
		    	console.log("Error loading process");
		    });
		};
  
  this.articleClicked =function(parameter){
	  console.log('Parameter '+parameter+' clicked');
  };
  
};


angular.module('unidaplan').controller('process', ['restfactory', process]);

})();