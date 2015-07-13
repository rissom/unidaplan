(function(){
'use strict';


function sampleChoser(sampleService,$translate,$scope,restfactory) {

	this.samples=[{id:9},{id:1}]
	
	var promise=restfactory.GET('/samples_by_name.json?type=1&name=1');
	var temp=this;
	promise.then(function(data){temp.samples=data.data});
};

        
angular.module('unidaplan').controller('sampleChoser',['sampleService','$translate','$scope','restfactory',sampleChoser]);

})();