(function(){
  'use strict';


var process = function(SampleService){
	
  this.samples=[];
	
  this.loadSamples = function(){
    console.log('to your service');
    SampleService.loadSamplesByName('Solarzelle','1');
    this.samples=SampleService.samples;
    },
    
  this.getSamples = function(){
    this.samples=SampleService.samples;	
    }
    
  this.test= {name:'hans',type:'wurst'}
    
};



angular.module('unidaplan').controller('process', ['SampleService', process]);

})();