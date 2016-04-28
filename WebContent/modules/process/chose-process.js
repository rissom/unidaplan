(function(){
'use strict';

function choseProcessController(restfactory,$state,ptypes,avProcessTypeService){
	
	var thisController = this;
	
	this.processTypes = ptypes;
	
	this.strings = [];
			
	
	this.keyUp = function(keyCode) {
		if (keyCode==13) {				// Return key pressed
			this.choseProcess();
		}
	};

	
		
	this.choseProcess = function() {
		var promise = restfactory.GET("process-by-number?number="+thisController.pnumber+"&type="+thisController.processType.id);
		promise.then( function(rest){
			console.log(rest.data);
			$state.go('process',{processID:rest.data.processid});	
			},function(){
				console.log("Error");
			});
	};
	
	
	
	// activate function (prefill selector)
	if (ptypes){
		if (ptypes[0]){
			this.processType = ptypes[0];
		}
	}
   
}  


angular.module('unidaplan').controller('choseProcessController',['restfactory','$state',
    'ptypes','avProcessTypeService',choseProcessController]);

})();