(function(){
'use strict';

function oExpController(restfactory,$rootScope,$translate,$scope,$state,$stateParams,experimentService,experiments) {
	
	var thisController = this;

	this.experiments = experiments;
	
	this.strings = [];
		
	this.statusItems=[$translate.instant("planning phase"),$translate.instant("planned"),
	                  $translate.instant("running"),$translate.instant("completed")]
	

	
	this.setStatus=function(status,experiment){
		experiment.status = status;
		var promise = restfactory.GET("change-experiment-status?id=" + experiment.id + "&status=" + status)
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
	
	
	this.cancelAdd = function(){
		this.editmode = false;
	};
	
	
	
	this.myExperiments = function() {  // returns all my experiments
		var myExps = [];
		var me = this.myName;
		angular.forEach(this.experiments, function(anExp) {
			if (anExp.creator == $rootScope.userid) {
				myExps.push(anExp);
			}
		});
		return myExps;
	};
	
	
	
	this.newExperiment = function(){
		var promise = experimentService.addExperiment();
		promise.then(function(rest){ 
				$state.go("experiment",{"experimentID":rest.data.id, "editmode" : "true"})
			},function(){
				console.log("error");
			}
		)
	};
	
	
	
	this.otherExperiments = function() {  // liefert alle meine Experimente zurück
		var otherExps = [];
		var me=this.myName;
		angular.forEach(this.experiments, function(anExp) {
			if (anExp.creator != $rootScope.userid) {
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
	
	

	 var reload = function() {
	 	var current = $state.current;
	 	var params = angular.copy($stateParams);
	 	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	 };
	 
};
    
        
angular.module('unidaplan').controller('oExpController',['restfactory','$rootScope','$translate','$scope','$state','$stateParams','experimentService','experiments',oExpController]);

})();