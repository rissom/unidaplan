(function(){
'use strict';

// How to build the ActivityService using the .service method
var sampleService = function($http){

  this.samples = function() { return [{id:5},{id:6},{id:7}]};

//  this.loadSamplesByName = function(type,name){
//	 return	$http.get('samples_by_name.json?type='+type+'&name='+name);
//  };
//  
    
};


angular.module('unidaplan').service('sampleService', ['$http',sampleService]);

})();