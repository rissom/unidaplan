(function(){
'use strict';

function sampleController(restfactory,sample,sampleService,$translate,$scope,key2string){
	
	var thisController = this;
	
	this.sample = {};
	
	this.strings = [];
		
	this.id = sample.id;
	this.parameters = sample.parameters;
	this.titleparameters = sample.titleparameters;
	this.strings = sample.strings;
	this.children = sample.children;
	this.ancestors = sample.ancestors;
	this.plans = sample.plans;
	this.deletable = sample.deletable;
	this.name = sample.name;
	this.next = sample.next;
	this.previous = sample.previous;
	this.typestringkey = sample.typestringkey;
	this.typeid = sample.typeid;
	
	
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			parameter.editing=false; 
			var oldValue=parameter.value;
			parameter.value=newValue;
			 if (parameter.pid) {
				var res = SampleServie.updateSampleParamter(parameter) 
				res.then(function(data, status, headers, config) {
						 },
						 function(data, status, headers, config) {
							parameter.value=oldValue;
							console.log('verkackt');
							console.log(data);
						 }
						);
			 } else {
				var res = sampleService.addSampleParameter(this.id,parameter);
					res.then(function(data) {
							 },
							 function(data) {
								parameter.value=oldValue;
								console.log('verkackt');
								console.log(data);
							 }
							);
			 }
		}
		if (keyCode===27) {		// Escape key pressed
			parameter.editing=false;			
		}
	}
	
	
	
	this.deleteSample = function()
	{  
		var promise = sampleService.deleteSample(sample.id);
		promise.then(function(data) {  			// success
				$state.go('experiments')	// go to experiments			
			},
				function(data) { 	 // fail
			    console.log("Error deleting Sample");
				console.log("Sample id: ",id);
				$state.go(error)
			}
		);
	}
	


	this.translate = function(lang) {

		var trtypename=key2string.key2string(thisController.typestringkey,thisController.strings)
			this.trtype=trtypename;
			if (this.next) 	   { this.next.trtypename=trtypename; }
			if (this.previous) { this.previous.trtypename=trtypename; }
			
		angular.forEach(this.parameters, function(parameter) {
			parameter.trname=key2string.key2string(parameter.stringkeyname,thisController.strings) 
		})
		angular.forEach(this.children, function(child) {
			child.trtypename=key2string.key2string(child.typestringkey,thisController.strings) 
		})	
		angular.forEach(this.ancestors, function(ancestor) {
			ancestor.trtypename=key2string.key2string(ancestor.typestringkey,thisController.strings) 
		})	
		angular.forEach(this.plans, function(plan) {
			plan.trname=key2string.key2string(plan.name,thisController.strings) 
		})	
	}

	
	
	$scope.$on('language changed', function(event, args) {
		thisController.translate(args.language);
	});
	
	
	
	this.saveParameter = function(parameter) {
		var promise = sampleService.saveParameter(parameter);
		promise.then(
				function(data) {
				},
				function(data) {
					console.log('verkackt');
					console.log(data);		
				}
		);
	};
	
	
	
	// activate function
	this.translate($translate.use()); // translate to active language

}  



angular.module('unidaplan').controller('sampleController',['restfactory','sample','sampleService','$translate','$scope','key2string','sample',sampleController]);

})();