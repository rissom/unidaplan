(function(){
'use strict';

function sampleController(sample,$state,$stateParams,$modal,$filter,types,sampleService,avSampleTypeService,
						  $translate,key2string,ptypes,avProcessTypeService){
	
	var thisController = this;
		
	if (sample.error) this.error=sample.error;
	this.parameters = sample.parameters;
	this.processes = $filter('orderBy')(sample.processes, 'date', false)
	this.titleparameters = sample.titleparameters;
	this.children = sample.children?sample.children:[];
	this.ancestors = sample.ancestors?sample.ancestors:[];
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
		var eSamples;
		if (mode=="ancestors"){ 		
			mSamples=this.ancestors;
			eSamples=this.children;
		} else {
			mSamples=this.children;
			eSamples=this.ancestors;
		}		
			
	    var modalInstance = $modal.open({
		    animation: false,
		    templateUrl: 'modules/modal-sample-choser/modal-sample-choser.html',
		    controller: 'modalSampleChoser as mSampleChoserCtrl',
		    size: 'lg',
		    resolve: {
		    	samples 	: function() { return mSamples; },
		        types       : function() { return types; },
		        except		: function() {
		        				var eSamples2=eSamples.slice(0);
		        				eSamples2.push({sampleid:sample.id,typeid:sample.typeid,name:sample.name});
		        				return eSamples2;
		        				},
		        mode		: function() { return "multiple"},
		        buttonLabel	: function() { return $translate.instant('assign to sample'); }
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
				var res = sampleService.updateSampleParameter(parameter)
				res.then(function(data, status, headers, config) {
						 },
						 function(data, status, headers, config) {
							parameter.value=oldValue;
							console.log('error');
							console.log(data);
						 }
						);
			 } else {
				var res = sampleService.addSampleParameter(sample.id,parameter);
					res.then(function(data) {
							 },
							 function(data) {
								parameter.value=oldValue;
								console.log('error');
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

	
	
	
	this.saveParameter = function(parameter) {
		var promise = sampleService.saveParameter(parameter);
		promise.then(
				function(data) {
				},
				function(data) {
					console.log('error');
					console.log(data);		
				}
		);
	};
	
	

}  



angular.module('unidaplan').controller('sampleController',['sample','$state','$stateParams','$modal','$filter','types',
     'sampleService','avSampleTypeService','$translate','key2string','ptypes','avProcessTypeService',sampleController]);

})();