(function(){
'use strict';

function oExpController(restfactory,$translate,$scope,$state,experimentService,experiments) {
	
	var thisController=this;

	this.experiments=experiments;
	
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
	
	this.deleteExperiment = function(experiment) {
		experimentService.deleteExperiment(experiment.id);
		$scope.$state = $state;
        $scope.$watch('$state.$current.locals.globals.experiments', function (experiments) {
          thisController.experiments = experiments;
        });
		$state.reload();
//		$scope.$watch('thisController.experiments');
	};
	
	
	this.translate = function(lang) {
		if (lang=='en') {
			this.statusItems=["planning phase","planned","running","completed"];
		}else{
			this.statusItems=["Planungsphase","geplant","läuft","abgeschlossen"];
		}
	};
	

};
    
        
angular.module('unidaplan').controller('oExpController',['restfactory','$translate','$scope','$state','experimentService','experiments',oExpController]);

})();