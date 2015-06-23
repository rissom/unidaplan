(function(){
'use strict';

angular.module('unidaplan').controller('showsamplecontroller',['$scope','restfactory',function($scope,restfactory){
	
	$scope.sample = { 'parameters':[ {name: 'A', id: 1, unit:'g', value:100}
									  ,{ name: 'B', unit:'', id:2, value: 200}],
					    	'next':{name: '0816', ID : 3 },
					    'previous':{name: '0815', ID : 2 },
					  'ancesterof':[{name: '0815', ID : 2 }],
					   'ancestors':[{name: '0816', ID : 3 }],
						   'plans':[333,444],
						    'type':'Petridish'					
				    }
	
	$scope.loadData = function(ID) {
		var promise = restfactory.GET("showsample?ID="+ID);
	    promise.then(function(rest) {
	    	$scope.sample = rest.data;
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
    $scope.articleClicked = function(article) {
    	console.log("articleClicked(article):",article);
    };
}]);
})();