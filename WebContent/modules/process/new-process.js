(function(){
'use strict';

function newProcessController(restfactory,$state,ptypes,avProcessTypeService){
	
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
		var d=new Date();
		var promise = restfactory.POST("add-process?processtypeid="+this.processType.id+"&timezone="+d.getTimezoneOffset());
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
                                                               'avProcessTypeService',newProcessController]);

})();