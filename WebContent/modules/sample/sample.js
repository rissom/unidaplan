(function(){
'use strict';

function sampleController(restfactory,$translate,$scope,$stateParams){
	
	var thisController = this;
	
	this.sample = {};
	
	this.strings = [];
			
	this.loadData = function() {			// Load data and filter Titleparameters
		var ID= $stateParams.sampleID;
		var promise = restfactory.GET("showsample.json?id="+ID);
	    promise.then(function(rest) {
	    	thisController.parameters = rest.data.parameters;
	    	thisController.titleparameters = rest.data.titleparameters;
	    	thisController.strings = rest.data.strings;
	    	thisController.children = rest.data.children;
	    	thisController.ancestors = rest.data.ancestors;
	    	thisController.plans = rest.data.plans;
	    	thisController.deletable = rest.data.deletable;
	    	thisController.next = rest.data.next;
	    	thisController.previous = rest.data.previous;
	    	thisController.translate($translate.use()); // translate to active language
	    }, function(rest) {
	    	thisController.sample.error = "Not Found!";
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
				var res = restfactory.POST('add-sample-parameter.json?sampleid='+this.sample.id,parameter);
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
		
	this.stringFromKey = function(stringkey,strings) {
		var keyfound=false;
		var returnString="@@@ no string! @@@";
		angular.forEach(strings, function(translation) {
			if (!keyfound && stringkey==translation.string_key) {
				returnString = translation.value;
				if (translation.language==$translate.use()) {
					keyfound=true;
				}
			}
		})
		return returnString;
	};
	
	

	this.translate = function(lang) {
		var trtypename=this.stringFromKey(this.sample.typestringkey,thisController.strings) 
		this.sample.trtype=trtypename;
			if (this.next) 	 { this.sample.next.trtypename=trtypename; }
			if (this.previous) { this.sample.previous.trtypename=trtypename; }
			
		angular.forEach(this.parameters, function(parameter) {
			parameter.trname=thisController.stringFromKey(parameter.stringkeyname,thisController.strings) 
		})
		angular.forEach(this.children, function(child) {
			child.trtypename=thisController.stringFromKey(child.typestringkey,thisController.strings) 
		})	
		angular.forEach(this.ancestors, function(ancestor) {
			ancestor.trtypename=thisController.stringFromKey(ancestor.typestringkey,thisController.strings) 
		})	
		angular.forEach(this.plans, function(plan) {
			plan.trname=thisController.stringFromKey(plan.name,thisController.strings) 
		})	
	}

	
	
	this.saveParameter = function(parameter) {
		var res = restfactory.POST('savesampleparameter.json',parameter);
		res.then(function(data, status, headers, config) {
		},function(data, status, headers, config) {
			console.log('verkackt');
			console.log(data);		
		});
	};
	
    
    
    //activation 
    this.loadData();
}  



angular.module('unidaplan').controller('sampleController',['restfactory','$translate','$scope','$stateParams',sampleController]);

})();