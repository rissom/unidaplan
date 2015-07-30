(function(){
'use strict';

function sampleController(restfactory,sampleService,$translate,$scope,$stateParams,key2string){
	
	var thisController = this;
	
	this.sample = {};
	
	this.strings = [];
			
	this.loadData = function() {			// Load data and filter Titleparameters
		var ID= $stateParams.sampleID;
		var promise = restfactory.GET("showsample.json?id="+ID);
	    promise.then(function(rest) {
	    	thisController.id = rest.data.id;
	    	thisController.parameters = rest.data.parameters;
	    	thisController.titleparameters = rest.data.titleparameters;
	    	thisController.strings = rest.data.strings;
	    	thisController.children = rest.data.children;
	    	thisController.ancestors = rest.data.ancestors;
	    	thisController.plans = rest.data.plans;
	    	thisController.deletable = rest.data.deletable;
	    	thisController.name = rest.data.name;
	    	thisController.next = rest.data.next;
	    	thisController.previous = rest.data.previous;
	    	thisController.typestringkey = rest.data.typestringkey;
	    	thisController.typeid = rest.data.typeid;
	    	thisController.translate($translate.use()); // translate to active language
	        var pSample = {"id"		  	   : rest.data.id,
		    			   "typeid"		   : rest.data.typeid,
		    			   "typestringkey" : rest.data.typestringkey,
		    			   "trtype"		   : thisController.trtype,
		    			   "name"		   : rest.data.name}
	    sampleService.pushSample(pSample);
	    }, function(rest) {
	    	thisController.error = "Not Found!";
	    });
	};
	

	$scope.$on('language changed', function(event, args) {
		thisController.translate(args.language);
	});
	
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			parameter.editing=false; 
			var oldValue=parameter.value;
			parameter.value=newValue;
			 if (parameter.pid) {
				var res = restfactory.POST('update-sample-parameter.json',parameter);
				res.then(function(data, status, headers, config) {
						 },
						 function(data, status, headers, config) {
							parameter.value=oldValue;
							console.log('verkackt');
							console.log(data);
						 }
						);
			 } else {
				var res = restfactory.POST('add-sample-parameter.json?sampleid='+$stateParams.sampleID,parameter);
					res.then(function(data, status, headers, config) {
							 },
							 function(data, status, headers, config) {
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
		var id=this.sample.id;
		var res = restfactory.GET("delete-sample?id="+id);
		res.then(function(data, status, headers, config) {   // success
						// gehe zur Experimentseite			
				 },
					function(data, status, headers, config) { 	 // fail
				    console.log("Error deleting Sample");
					console.log("Sample id: ",id);
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

	
	
	this.saveParameter = function(parameter) {
		var res = restfactory.POST('savesampleparameter.json',parameter);
		res.then(
				function(data, status, headers, config) {
				},
				function(data, status, headers, config) {
					console.log('verkackt');
					console.log(data);		
				}
		);
	};
	
    
    
    //activation 
    this.loadData();
}  



angular.module('unidaplan').controller('sampleController',['restfactory','sampleService','$translate','$scope','$stateParams','key2string',sampleController]);

})();