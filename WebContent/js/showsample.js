(function(){
'use strict';

function samplecontroller(restfactory) {
	
	this.sample = { 'parameters':[ {name: 'A', id: 1, unit:'g', value:100}
									  ,{ name: 'B', unit:'', id:2, value: 200}],
					    	'next':{name: '0816', ID : 3 },
					    'previous':{name: '0815', ID : 2 },
					  'ancesterof':[{name: '0815', ID : 2 }],
					   'ancestors':[{name: '0816', ID : 3 }],
						   'plans':[333,444],
						    'type':'Petridish'					
				  };
	
	
	this.loadData = function(ID) {
		var promise = restfactory.GET("showsample.json?ID="+ID);
		var thisSampleController = this;
	    promise.then(function(rest) {
	    	thisSampleController.sample = rest.data;
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	
    this.articleClicked = function(article) {
    	console.log("articleClicked(article):",article);
    } 
}  

angular.module('unidaplan').controller('showsamplecontroller',['restfactory',samplecontroller]);

})();