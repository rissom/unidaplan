(function(){
'use strict';

// How to build the ActivityService using the .service method
var userService = function($http){

  this.username = "";
  
  this.language = "en";
  
  this.getData =

//  this.loadSamplesByName = function(type,name){
//	 return	$http.get('samples_by_name.json?type='+type+'&name='+name);
//  };
//  
    
};


angular.module('unidaplan').service('userService', ['$http',userService]);

})();