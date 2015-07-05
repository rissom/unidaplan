(function(){
'use strict';
angular.module('unidaplan').controller('expcontroller',['$scope','restfactory',function($scope,restfactory){
	
	this.experiments =  [{"creator":"Thorsten Rissom","name":"Erstes Experiment","id":1},
	                   {"creator":"Thorsten Rissom","name":"Zweites Experiment","id":2}];			

	$scope.myName='Thorsten Rissom';
	
	$scope.loadData = function(ID) {
		var promise = restfactory.GET("experiments.json?ID="+ID);
	    promise.then(function(rest) {
	    	$scope.experiments = rest.data;
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	this.number_of_own_experiments = function() {		
		var i = this.experiments.length;
		var x = 0;
	    while (i--) {
	       if (this.experiments[i].creator == $scope.myName) {
		       x++;
		   }
	    }
	    return x;
	};
	
	
    $scope.articleClicked = function(article) {
    	console.log("articleClicked(article):",article);
    };
}]);
})();