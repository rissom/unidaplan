(function(){
  'use strict';

function process($state,$stateParams,$translate,avSampleTypeService,types,experiments,$uibModal,processData,restfactory,processService){
  
	var thisController = this;
	
	this.newNumber = processData.pnumber;
  
	this.deletable = processData.deletable;
	
	this.editable = processData.editable;
  	
	this.files = processData.files;
	
	this.process = processData;
	
	this.date = {data:{date:processData.date}, id: processData.dateid};
 
	this.stati = [{index: 1, stringf : function() {return $translate.instant("OK")}},
				  {index: 2, stringf : function() {return $translate.instant("attention")}},
				  {index: 3, stringf : function() {return $translate.instant("failed")}}];
	
    this.status = this.stati[processData.status-1];
	
    this.newStatus = this.stati[processData.status-1];

    
    
  	this.assign = function(){
  		var samples2assign={samples:this.process.samples, id:processData.id};
  		var promise = restfactory.POST("add-sample-to-process",samples2assign);
  	};

  	
  	
	this.deleteFile = function (fileID){
		var promise = processService.deleteFile(fileID);
		promise.then (function(){reload()});
	}
	
	
	// add opid-field to all sample related parameters
	for (var i = 0; i<this.process.samples.length; i++){
		for (var j = 0; j<this.process.samples[i].parameters.length; j++){
			this.process.samples[i].parameters[j].opid = this.process.samples[i].opid
			if (typeof(this.process.samples[i].parameters[j].unit)!="undefined"){
				this.process.samples[i].parameters[j].unit = this.process.fields[j].unit;
			}
			if (typeof(this.process.samples[i].parameters[j].unitf)!="undefined"){
				this.process.samples[i].parameters[j].unitf = this.process.fields[j].unitf;
			}
		}
	}
  
	
	
	this.openDialog = function () {
	  
		var modalInstance = $uibModal.open({
			animation: false,
			templateUrl: 'modules/modal-sample-choser/modal-sample-choser.html',
			controller: 'modalSampleChoser as mSampleChoserCtrl',
			size: 'lg',
			resolve: {
				samples : function() { return thisController.process.samples; },
		        types   : function() { return types; },
		        experiments : function() { return experiments; },
		        	except	: function() { return []; },
			    buttonLabel : function() { return $translate.instant('assign to process'); },
			    mode : function() { return 'multiple'; }
		    }		        
		});
	  
		modalInstance.result.then(function (result) {  // get the new Samplelist + Info if it is changed from Modal. 
			thisController.process.samples = result.chosen;
			if (result.changed) { thisController.assign(); }
		}, function () {
			console.log('Strange Error: Modal dismissed at: ' + new Date());
		});
	};

  
  
	// return the translated name string of a type for a sample
	this.getType = function(sample){
		return avSampleTypeService.getType(sample,types);
	};
  
  
  
  	this.paramKeyUp = function(keyCode,newValue,parameter) {
  	    // TODO: replace processnumber with integerparameter declarative
		if (keyCode===13) {				// Return key pressed
			parameter.editing = false; 
			var oldValue = parameter.value;
			parameter.value = newValue;
			var res = processService.saveParameter(this.process.id,parameter);
			res.then(function(data, status, headers, config) {
				 },
				 function(data, status, headers, config) {
					parameter.value=oldValue;
					console.log('error');
					console.log(data);
				 }
			);
		}
		
		if (keyCode === 27) {		// Escape key pressed
			parameter.editing = false;		
		}
	};
	
	
	
	this.dateUpdate = function(){
	    var promise = processService.saveParameter(thisController.process.id,thisController.date);
	    promise.then(
                function(data) {
                    reload();
                },
                function(data) {
                    console.log('error');
                    console.log(data);      
                }
        );
	}
	       
  
  
  
	this.deleteProcess = function(){
		return processService.deleteProcess(this.process.id);
	};
  
  
  	
  	this.keyUpNumber = function(keyCode){
		if (keyCode === 13) {				// Return key pressed
			thisController.editNumber = false; 
			var oldValue = thisController.process.pnumber;
			thisController.process.pnumber = thisController.newNumber;
			var res = processService.setNumber(thisController.process.id,thisController.newNumber);
			res.then(function(rest) {reload();}, function(){thisController.process.pnumber=oldValue; reload();});
		}			 
		if ( keyCode === 27 ) {		// Escape key pressed
			thisController.editNumber = false; 		
		}
	};
  		
  	
	
  	this.saveParameter = function(parameter) {
		var promise = processService.saveParameter(this.process.id,parameter);
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
  
	
  
  	this.setStatus = function(){
  		var promise = processService.setStatus(processData,thisController.newStatus.index);
		promise.then(function(){reload();});
  	};
  
  	
  	
  	this.pupdate = function(parameter) {
  		var promise = processService.savePOParameter(parameter);
		promise.then(reload);
  	}
  	
  	
	
	this.upload = function(element) {
		thisController.file=element.files[0].name;
		var file = element.files[0].name;
		var xhr = new XMLHttpRequest();
		xhr.addEventListener('load', reload);
		xhr.open("POST", 'upload-file2?process=' + processData.id); // xhr.open("POST", 'upload-file',true); ???
		
		// formdata
		var formData = new FormData();
		formData.append("file", element.files[0]);
		xhr.send(formData);
    };

    
  
	var reload = function() {
		var current = $state.current;
		var params = angular.copy($stateParams);
		return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
	};
 
}

angular.module('unidaplan').controller('process', ['$state','$stateParams','$translate','avSampleTypeService','types',
                           'experiments','$uibModal','processData','restfactory','processService',process]);

})();