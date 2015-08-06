(function(){
'use strict';

function choseProcessController(restfactory,$state,ptypes,avProcessTypeService){
	
	var thisController = this;
	
	this.processTypes = ptypes;
	
	this.strings = [];
			
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			this.addProcess();
		}
	}

		
	this.choseProcess = function() {
				
	}
	
	// activate function (prefill selector)
	if (ptypes){
		if (ptypes[0]){
			this.processType = ptypes[0];
		}
	}
   
}  


angular.module('unidaplan').controller('choseProcessController',['restfactory','$state','ptypes',
                                                               'avProcessTypeService',choseProcessController]);

})();