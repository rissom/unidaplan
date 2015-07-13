(function(){
'use strict';

function experiment(restfactory,$translate,$stateParams) {
	
	this.experiments =  [];		

	this.strings = [];
	
	this.ID = function(){
		return $stateParams.experimentID;
	}
	
	this.myName='Thorsten Rissom';
	
	this.loadData = function() {
		var promise = restfactory.GET("experiments.json"),
			expsCtrl=this;

		
	    promise.then(function(rest) {
	    	expsCtrl.experiments = rest.data.experiments;
	    	expsCtrl.strings = rest.data.strings;
	    	expsCtrl.translate();
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	this.translate = function() {
		
	
	}

};
    
        
angular.module('unidaplan').controller('expController',['restfactory','$translate','$stateParams',experiment]);

})();