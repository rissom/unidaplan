(function(){
'use strict';

// How to build the ActivityService using the .service method
var SampleService = function($http){

	
  this.loadSamplesByName = function(type,name){
	  var thisSampleService=this;
	  thisSampleService.samples=[];
	  $http.get('samples_by_name.json?type='+type+'&name='+name).success(function(data){
		  thisSampleService.samples = data;
	  });
  };
    
};


angular.module('unidaplan').service('SampleService', ['$http',SampleService]);


//How to build the ActivityService using the .factory method
// var ActivityService = function($http){
//   var Activity = {};
//   Activity.getAll = function(){
//     return $http.get('../test/api/posts.json');
//   };
//   return Activity;
// };

// angular
//   .module('myApp')
//   .factory('ActivityService', [
//     '$http',
//     ActivityService
//   ]);






//How to build the ActivityService using the .provider method
// var Activity = function(){
//   var api;
//   var ActivityConfig = {};
//   ActivityConfig.setAPI = function(value){
//     api = value;
//   };
//   //the $get method is what actually creates the Service
//   ActivityConfig.$get = ['$http',
//     function($http){
//       var Activity = {};
//       Activity.getAll = function(){
//         return $http.get(api);
//       };
//       return Activity;
//     }];
//   return ActivityConfig;
// };

// angular
//   .module('myApp')
//   .provider('Activity', Activity);


})();