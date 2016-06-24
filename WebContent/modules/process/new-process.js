(function(){
'use strict';

function newProcessController(restfactory,$state,ptypes,avProcessTypeService,processService){
	
	var thisController = this;
	
	this.processTypes = ptypes;
	
	this.strings = [];
	
	this.hallo = 'Hallo!';			
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			this.addProcess();
		}
	}

		
	this.addProcess = function() {
		
		var promise = processService.addProcess(this.processType.id, this.recipe);
		promise.then(function(rest){
			if (rest.data.status=="ok") {
				$state.go('process',{processID:rest.data.id})
			}
		},function(){
			console.log ("failure")
		})		
	}
	
	// activate function (prefill selector)
	if (ptypes){
		if (ptypes[0]){
			this.processType = ptypes[0];
		}
	}
   
}  


angular.module('unidaplan').controller('newProcessController',['restfactory','$state','ptypes',
                              'avProcessTypeService','processService',newProcessController]);

})();