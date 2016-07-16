(function(){
'use strict';

function sampleController(sample,$state,$stateParams,$uibModal,$filter,types,sampleService,avSampleTypeService,
						  $translate,key2string,ptypes,avProcessTypeService){
	
	var thisController = this;
		
	if (sample.error) this.error=sample.error;
	this.parametergroups = sample.parametergroups;
	this.editable = sample.editable;
	this.files = sample.files;
	this.processes = $filter('orderBy')(sample.processes, 'date', false);
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
		

	
	// store ancestors in database
	this.assignAncestors = function(ancestors){
		var a2 = [];
		for (var i = 0; i < ancestors.length; i++) {
			a2.push(ancestors[i].sampleid);
		}
		sampleService.addAncestors(sample.id,a2);
	};
	
	
	
	// store children in database
	this.assignChildren = function(children){
		var c2=[];
		for (var i=0; i<children.length; i++) {
			c2.push(children[i].sampleid);
		}		
		sampleService.addChildren(sample.id,c2);
	};
	
	
	
	this.deleteFile = function (fileID){
		var promise = sampleService.deleteFile(fileID);
		promise.then (function(){reload()});
	}
	
	
	
	this.deleteSample = function()
	{  
		var promise = sampleService.deleteSample(sample.id);
		promise.then(function(data) {  			// success
				$state.go('sampleChoser');	// go to experiments			
			},
				function(data) { 	 // fail
			    console.log("Error deleting Sample");
				$state.go(error);
			}
		);
	};
	

	
	// returns the duration between 2 processes
	this.getDuration = function(index){
		var duration = 0;
		if (this.processes.length>index+1) {
			duration = this.processes[index+1].date-this.processes[index].date;
		}
		return duration;
	};
	
	
	
	// returns the translated name of a process
	this.getProcessType = function(process){
		return avProcessTypeService.getProcessType(process,ptypes);
	};
	

	
	this.getType = function(sample){
		return avSampleTypeService.getType(sample,types);
	};
	
	
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode === 13) {				// Return key pressed
			parameter.editing = false;
			var oldValue 
			if (typeof(parameter.data) != "undefined") {
				oldvalue = parameter.data.value;
			} else {
				parameter.data = {};
			} 
			parameter.data.value = newValue;
			var res;
			res = sampleService.saveParameter(sample.id,
					{pid : parameter.parameterid,
					 data : {value:parameter.data.value}});
			res.then(function() { reload(); },
					 function() {
						parameter.data.value = oldValue;
						console.log('error');
					}
			);
		}
		if (keyCode===27) {		// Escape key pressed
			parameter.editing=false;			
		}
	};
	
	

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
			
	    var modalInstance = $uibModal.open({
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
		        mode		: function() { return "multiple";},
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

	
	
	this.saveParameter = function(parameter) {
		var promise = sampleService.saveParameter(sample.id,parameter);
		promise.then(
				function(data) {
					reload();
				},
				function(data) {
					console.log('error');
					console.log(data);
				}
		);
	};
	
	
	
	this.upload = function(element) {
		thisController.file=element.files[0].name;
		var file=element.files[0].name;
		var xhr = new XMLHttpRequest();
		xhr.addEventListener('load', function(event) {
			reload();
		});
		
		xhr.open("POST", 'upload-file2?sample='+$stateParams.sampleID); // xhr.open("POST", 'upload-file',true); ???
		
		// formdata
		var formData = new FormData();
		formData.append("file", element.files[0]);
		xhr.send(formData);
    };

	
	
	this.updateSampleParameter = sampleService.saveSampleParameter;
	
	
	
	var reload=function() {
	    var current = $state.current;
	    var params = angular.copy($stateParams);
	    return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};
	

}  



angular.module('unidaplan').controller('sampleController',['sample','$state','$stateParams','$uibModal','$filter','types',
     'sampleService','avSampleTypeService','$translate','key2string','ptypes','avProcessTypeService',sampleController]);

})();