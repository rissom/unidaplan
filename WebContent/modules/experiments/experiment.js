(function(){
'use strict';

function experiment2(restfactory,$translate) {
	
	this.experiments =  [];			

	this.strings = [];
	
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
    
        
angular.module('unidaplan').controller('exp2controller',['restfactory','$translate',experiment2]);

})();