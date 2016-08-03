(function(){
'use strict';


function formulaModalController($translate,$uibModalInstance,languages,formula,paramGroups,parameters) {

		
	this.paramGroups = paramGroups;
	
	var thisController = this;

	this.formula = formula;
	
	this.newFormula = formula;
	
	var thisController = this;
	
	this.parameters = parameters;
	
	this.cancel = function(){ // Parameters where not changed
	    $uibModalInstance.close({ok:false});
	};
	
	if (parameters) {
		this.selectedParameters = parameters.slice(0);
	} else {
		this.selectedParameters = [];
	}

	
	this.insertInFormula = function (string){
		var myField = angular.element(document.querySelector('#target'))[0];
		if (myField.selectionStart || myField.selectionStart == '0') {
	        var startPos = myField.selectionStart;
	        var endPos = myField.selectionEnd;
	        myField.value = myField.value.substring(0, startPos)
	            + string
	            + myField.value.substring(endPos, myField.value.length);
	    } else {
	        myField.value += string;
	    }
		this.newFormula = myField.value;
	}
	
	
	this.insertParameter = function (parameter){
		var myField = angular.element(document.querySelector('#target'))[0];
		if (myField.selectionStart || myField.selectionStart == '0') {
	        var startPos = myField.selectionStart;
	        var endPos = myField.selectionEnd;
	        myField.value = myField.value.substring(0, startPos)
	            + "p" + parameter.id
	            + myField.value.substring(endPos, myField.value.length);
	    } else {
	        myField.value += "p" + parameter.id;
	    }
		this.newFormula = myField.value;
	}
	
	
	
	this.saveFormula = function(){    // pass the new list of parameters if it has changed
		var ok = true;
	    $uibModalInstance.close({ok:ok, formula : thisController.newFormula});
	};


	
	
	this.showParamGrp = function(parameter){
		for (var i = 0; i < thisController.paramGroups.length; i++)
			if (parameter.parametergroup === thisController.paramGroups[i].id)
				return this.paramGroups[i].namef();
	};
	
	
	
	this.cancel = function(){
	    $uibModalInstance.close(thisController.formula);
	}
	
};

        
angular.module('unidaplan').controller('formulaModalController',['$translate','$uibModalInstance','languages',
                           'formula','paramGroups','parameters',formulaModalController]);

})();