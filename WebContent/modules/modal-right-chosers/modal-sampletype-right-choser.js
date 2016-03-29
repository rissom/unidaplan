(function(){
'use strict';


function mSampletypeRightChoser($translate,$uibModalInstance,languages,restfactory,sampletypes) {
	
	this.sampletypes=sampletypes;

	var thisController=this;		
	
	this.radioModel = 'Left';
	
	this.cancel=function(){ // Parameters where not changed
	    $uibModalInstance.close({chosen:[]});
	};
	
	
	
	this.assignRights=function(){    // pass the new list of parameters if it has changed
	    $uibModalInstance.close({chosen: this.selectedRights});
	};
}

        
angular.module('unidaplan').controller('mSampletypeRightChoser',['$translate','$uibModalInstance','languages',
                            'restfactory','sampletypes',mSampletypeRightChoser]);

})();