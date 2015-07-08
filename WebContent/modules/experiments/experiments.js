(function(){
'use strict';

function experiments(restfactory,lang) {
	
	this.experiments =  [];			

	this.strings = [];
	
	this.myName='Thorsten Rissom';
	
	this.loadData = function() {
		var promise = restfactory.GET("experiments.json");
		var expsCtrl=this;
	    promise.then(function(rest) {
	    	expsCtrl.experiments = rest.data.experiments;
	    	expsCtrl.strings = rest.data.strings;
	    	expsCtrl.translate();
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	this.myexperiments = function() {  // liefert alle meine Experimente zurück
		var myExps=[];
		var me=this.myName;
		angular.forEach(this.experiments, function(anExp) {
			if (anExp.creator==me) {
				myExps.push(anExp);
			}
		});
		return myExps;
	}
	
	
	this.otherexperiments = function() {  // liefert alle meine Experimente zurück
		var otherExps=[];
		var me=this.myName;
		angular.forEach(this.experiments, function(anExp) {
			if (anExp.creator!=me) {
				otherExps.push(anExp);
			}
		});
		return otherExps;
	}
	
	
	this.translate = function() {
		var strings=this.strings;
		var exps=this.experiments;
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