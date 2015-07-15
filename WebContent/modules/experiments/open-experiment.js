(function(){
'use strict';

function oExpController(restfactory,$translate,$scope) {
	
	this.experiments =  [];			

	this.strings = [];
	
	this.myName='Thorsten Rissom';
	
	this.statusItems=["planning phase","planned","running","completed"]
	
	this.setStatus=function(status,experiment){
		experiment.status=status;
		var promise = restfactory.GET("change-experiment-status?id="+experiment.id+"&status="+status)
	    promise.then(function(rest) {
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	}
	
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
	
	var thisOExpCtrl = this;
	$scope.$on('language changed', function(event, args) {
		thisOExpCtrl.translate(args.language);
	});
	
	
	this.getStatus = function(experiment) {
		return this.statusItems[experiment.status];
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
		if (lang=='en') {
			this.statusItems=["planning phase","planned","running","completed"];
		}else{
			this.statusItems=["Planungsphase","geplant","läuft","abgeschlossen"];
		}
	};
	
	//activate function:
	this.loadData();

};
    
        
angular.module('unidaplan').controller('oExpController',['restfactory','$translate','$scope',oExpController]);

})();