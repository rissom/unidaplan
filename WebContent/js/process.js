(function(){
  'use strict';


var process = function(SampleService ){
	
  this.samples=[];
	
  this.loadSamples = function(){
    console.log('to your service');
    SampleService.loadSamplesByName('Solarzelle','1');
    this.samples=SampleService.samples;
    },
    
  this.getSamples = function(){
    this.samples=SampleService.samples;	
  },
    
  this.sayHello = function(){
	  console.log('Hello');
  },
  
  this.loadProcess = function($scope,restfactory,id){
//			var promise = restfactory.GET("process.json?id="+ID);
//		    promise.then(function(rest) {
//		    	$scope.process = rest.data;
//		    }, function(rest) {
		    	console.log("aufruf");
//		    });
		}
//		$http.get('process.json?id='+id)
  };



angular.module('unidaplan').controller('process', ['SampleService', process]);


})();