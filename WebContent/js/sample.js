(function(){
'use strict';

function samplecontroller(restfactory) {
	
	
	this.sample = {};
	
	
	this.loadData = function(ID) {
		var promise = restfactory.GET("showsample.json?id="+ID);
		var thisSampleController = this;
	    promise.then(function(rest) {
	    	thisSampleController.sample = rest.data;
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	this.sayHello = function() {
		console.log('Hello')
	};
	
	
    this.articleClicked = function(article) {
    	console.log("articleClicked(article):",article);
    }; 
}  


angular.module('unidaplan').controller('samplecontroller',['restfactory',samplecontroller]);

})();