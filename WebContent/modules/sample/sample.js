(function(){
'use strict';

function sampleController(sample,$state,$modal,$filter,types,sampleService,avSampleTypeService,$translate,$scope,key2string,ptypes,avProcessTypeService){
	
	var thisController = this;
		
		
	this.parameters = sample.parameters;
	this.processes = $filter('orderBy')(sample.processes, 'date', false)
	this.titleparameters = sample.titleparameters;
	this.children = sample.children;
	this.ancestors = sample.ancestors;
	this.plans = sample.plans;
	this.deletable = sample.deletable;
	this.name = sample.name;
	this.next = sample.next;
	if (this.next){this.next.typeid = sample.typeid;}
	this.previous = sample.previous;
	if (this.previous) {this.previous.typeid = sample.typeid;}
	this.typestringkey = sample.typestringkey;
	this.typeid = sample.typeid;

	
	
	// returns the translated name of a process
	this.getProcessType = function(process){
		return avProcessTypeService.getProcessType(process,ptypes);
	}
	
	
	
	// returns the duration between 2 processes
	this.getDuration = function(index){
		var duration = 0;
		if (this.processes.length>index+1) {
			duration = this.processes[index+1].date-this.processes[index].date;
		}
		return duration;
	}
	

	this.openDialog = function (mode) {			
		var mSamples;
		if (mode=="ancestors"){ 		
			var mSamples=this.ancestors
		} else {
			mSamples=this.children
		}		
			
	    var modalInstance = $modal.open({
		    animation: false,
		    templateUrl: 'modules/modal-sample-choser/modal-sample-choser.html',
		    controller: 'modalSampleChoser as mSampleChoserCtrl',
		    size: 'lg',
		    resolve: {
		    	samples 	  : function() {
		    					return mSamples; },
		        types         : function() {
		        				return types; },
		        except		  : function() {
		        				return {sampleid:sample.id,
		        						typeid:sample.typeid,
		        						name:sample.name}
		        				},
		        buttonLabel	  : function() { 
		        				return 'assign to sample';
		        				}
		    }		        
		});
	    
	  	modalInstance.result.then(function (result) {  // get the new Samplelist + Info if it is changed from Modal. 
			if (mode=="ancestors"){ 
				thisController.ancestors=result.chosen;
				if (result.changed) {
		    	    thisController.assignAncestors(result.chosen);
		        }
			} else {
				thisController.children=result.chosen;
				if (result.changed) {
		    	    thisController.assignChildren(result.chosen);
		        }
			}
	        
	    }, function () {
	        console.log('Strange Error: Modal dismissed at: ' + new Date());
	    });
	};

	
	
	// store ancestors in database
	this.assignAncestors = function(ancestors){
		var a2=[];
		for (var i=0; i<ancestors.length; i++) {
			a2.push(ancestors[i].sampleid);
		}
		console.log ("sample",sample);
		sampleService.addAncestors(sample.id,a2);
	}
	
	
	
	// store children in database
	this.assignChildren = function(children){
		var c2=[];
		for (var i=0; i<children.length; i++) {
			c2.push(children[i].sampleid);
		}		
		sampleService.addChildren(sample.id,c2);
	}
	
	
	
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
				$state.go('openExperiment')	// go to experiments			
			},
				function(data) { 	 // fail
			    console.log("Error deleting Sample");
				$state.go(error)
			}
		);
	}
	

	
	this.getType = function(sample){
		return avSampleTypeService.getType(sample,types);
	}

	
	
	this.translate = function(lang) {			
		angular.forEach(this.parameters, function(parameter) {
			parameter.trname=key2string.key2string(parameter.stringkeyname,sample.strings) 
		})
		angular.forEach(this.plans, function(plan) {
			plan.trname=key2string.key2string(plan.name,sample.strings) 
		})
//		angular.forEach(this.plans, function(plan) {
//			plan.trname=key2string.key2string(plan.name,sample.strings) 
//		})	
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



angular.module('unidaplan').controller('sampleController',['sample','$state','$modal','$filter','types',
     'sampleService','avSampleTypeService','$translate','$scope','key2string','ptypes','avProcessTypeService',sampleController]);

})();