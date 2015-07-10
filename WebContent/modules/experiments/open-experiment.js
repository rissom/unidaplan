(function(){
'use strict';

function openExperiment(restfactory,$translate,$scope) {
	
	this.experiments =  [];			

	this.strings = [];
	
	this.myName='Thorsten Rissom';
	
	
	this.loadData = function() {
		var promise = restfactory.GET("experiments.json"),
		 	expsCtrl=this;
		
		
	    promise.then(function(rest) {
	    	expsCtrl.experiments = rest.data.experiments;
	    	expsCtrl.strings = rest.data.strings;
	    	expsCtrl.translate($translate.use());
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	$scope.$on('language changed', function(event, args) {
		$scope.oexpCtrl.translate(args.language);
	});
	
	this.getStatus = function(experiment) {
		var status='-';
		switch (experiment.status) {
			case 0  : status="planning phase"; break;
			case 1  : status="planned"; break;
			case 2  : status="running"; break; 
		    default : status="finished"; }
		return status;
	};
	
	
	this.myexperiments = function() {  // returns all my experiments
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
	};
	
	
	this.translate = function(lang) {
		var strings=this.strings;
		var exps=this.experiments;
		angular.forEach(exps, function(anExp) {
			angular.forEach(strings, function(translation) {
				if (anExp.name==translation.string_key && translation.language==lang)
					{anExp.trname=translation.value;}
			})
		})		
	};

};
    
        
angular.module('unidaplan').controller('expcontroller',['restfactory','$translate','$scope',openExperiment]);

})();