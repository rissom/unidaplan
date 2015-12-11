(function(){
'use strict';

function oExpController(restfactory,$translate,$scope,$state,$stateParams,experimentService,experiments) {
	
	var thisController=this;

	this.experiments=experiments;
	
	this.strings = [];
	
	this.myName='Thorsten Rissom';
	
	this.statusItems=[$translate.instant("planning phase"),$translate.instant("planned"),
	                  $translate.instant("running"),$translate.instant("completed")]
	

	
	this.setStatus=function(status,experiment){
		experiment.status=status;
		var promise = restfactory.GET("change-experiment-status?id="+experiment.id+"&status="+status)
	    promise.then(function(rest) {
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	
	
	$scope.$on('language changed', function(event, args) {
		thisController.statusItems=[$translate.instant("planning phase"),$translate.instant("planned"),
	                  $translate.instant("running"),$translate.instant("completed")]
	});
	
	
	
	this.getStatus = function(experiment) {
		return this.statusItems[experiment.status];
	};
	
	
	
	this.addExperiment=function(){
		this.editmode=true;
	};
	
	
	
	this.cancelAdd=function(){
		this.editmode=false;
	};
	
	
	
	this.newExperiment=function(){
		var promise= experimentService.addExperiment();
		promise.then(function(){reload();},function(){console.log("error");})
		this.editmode=false;
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
	};
	
	
	
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
		var promise=experimentService.deleteExperiment(experiment.id);
		promise.then(function(){
				reload();
			},
			function(){
				console.log("error deleting experiment");
			})       
//		$scope.$watch('thisController.experiments');
	};
	
	

	 var reload=function() {
	 	var current = $state.current;
	 	var params = angular.copy($stateParams);
	 	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	 };
	 
};
    
        
angular.module('unidaplan').controller('oExpController',['restfactory','$translate','$scope','$state','$stateParams','experimentService','experiments',oExpController]);

})();