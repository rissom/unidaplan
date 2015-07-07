(function(){
'use strict';

function experiments(restfactory,lang) {
	
	this.experiments =  {experiments:[{"creator":"Thorsten Rissom","trname":"Platzhalter Experiment","id":1},
	                   {"creator":"Thorsten Rissom","trname":"Platzhalter Experiment","id":2}]};			

	this.myName='Thorsten Rissom';
	
	this.loadData = function() {
		var promise = restfactory.GET("experiments.json");
		var expsCtrl=this;
	    promise.then(function(rest) {
	    	expsCtrl.experiments = rest.data;
	    	expsCtrl.translate();
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	
	this.number_of_own_experiments = function() {		
		var i = this.experiments.experiments.length;
		var x = 0;
	    while (i--) {
	       if (this.experiments.experiments[i].creator == this.myName) {
		       x++;
		   }
	    }
	    return x;
//		return 2;
	};
	
	
	this.translate = function() {
		var strings=this.experiments.strings;
		var exps=this.experiments.experiments;
		angular.forEach(exps, function(anExp) {
			angular.forEach(strings, function(translation) {
				if (anExp.name==translation.string_key && translation.language==lang)
					{anExp.trname=translation.value;}
			})
		})	
	}

};
    
        
angular.module('unidaplan').controller('expcontroller',['restfactory','lang',experiments]);

})();