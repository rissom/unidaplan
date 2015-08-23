(function(){
'use strict';

var experimentService = function (restfactory,$q,$translate,key2string) {
	// restfactory is a wrapper for $html.

	var thisController=this;
	
	
	this.getExperiments = function() {
        var defered=$q.defer();
		var promise = restfactory.GET("experiments.json");
		 		
	    promise.then(function(rest) {
	    	thisController.experiments = rest.data.experiments;
	    	thisController.expsStrings = rest.data.strings;
	    	thisController.translateExps();
	    	defered.resolve(thisController.experiments)
	    }, function(rest) {
	    });
		return defered.promise;
	};
	
	
	
	this.getExperiment = function(id) {
        var defered=$q.defer();
    	    	var thisController=this;
    			var promise = restfactory.GET("experiment?id="+id);
    	    	promise.then(function(rest) {
	    	    	thisController.experiment = rest.data.experiment;
	    	    	thisController.strings = rest.data.strings;
	    	    	thisController.translate();
	    	    	defered.resolve(thisController.experiment)
    	    	}, function(rest) {    	    		
    	    		console.log("Error loading experiment");
    	    		defered.reject({"error":"Error loading experiment"});
    	    	});
		return defered.promise;
	}
	
    
	// save experiment for "recent experiments"
	this.pushExperiment = function(experiment){
		var i;
		var found=false;
		if (this.recentExperiments==undefined) {
			this.recentExperiments=[]
		}
		var tExperiment={"id":experiment.id,"number":experiment.number,"trname":experiment.trname,"name":experiment.name};
		for (i=0;i<this.recentExperiments.length;i++){
			if (this.recentExperiments[i].number==tExperiment.number) {
				found=true			
			}
		}
		if (!found) {
			this.recentExperiments.push(tExperiment);
		}
		if (this.recentExperiments.length>20){
			this.recentExperiments.slice(0,this.recentExperiments.length-20);
		}
	}
	
	
	
	// delete an experiment (also from recent experiments)
	this.deleteExperiment = function(id){
			if (this.recentExperiments!=undefined) {
				for (var i=0;i<this.recentExperiments.length;i++){
					if (this.recentExperiments[i].id==id){
						this.recentExperiments.splice(i,1);
					}
				}
			}
		return restfactory.POST("delete-experiment?id="+id);
	}
	
	
	
	this.translate = function() {
		this.experiment.trname=key2string.key2string(this.experiment.name,this.strings);
		angular.forEach(this.experiment.parameters, function(parameter) {
			parameter.trname=key2string.key2string(parameter.stringkeyname,thisController.strings);
		})
		if (this.recentExperiments!=undefined){
			angular.forEach(this.recentExperiments, function(experiment) {
				experiment.trname=key2string.key2string(experiment.name,thisController.strings);
			})
		}
	}
	
	this.translateExps = function() {
		var strings=this.expsStrings;
		var exps=this.experiments;
		angular.forEach(exps, function(anExp) {
			angular.forEach(strings, function(translation) {
				if (anExp.name==translation.string_key && translation.language==$translate.use())
					{anExp.trname=translation.value;}
			})
		})	
//		if ($translate.use()=='en') {
//			this.statusItems=["planning phase","planned","running","completed"];
//		}else{
//			this.statusItems=["Planungsphase","geplant","läuft","abgeschlossen"];
//		}
	};
	
}


angular.module('unidaplan').service('experimentService', ['restfactory','$q','$translate','key2string',experimentService]);

})();