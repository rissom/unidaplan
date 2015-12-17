 (function(){
'use strict';

/**
 * directive tparameter
- editable Boolean
- Einheit Boolean
- parameter {datatype:?? , value, tz, timestamp ....}

 */



var tparameter = function() {
	return {
		restrict: 'E',
		template: '{{parameter}}',
		scope:{
			parameter:'='
		},
		controller: function(){}

	};
	controllerAs: 'uparameterCtrl'
};
    
        
angular.module('unidaplan').directive('tparameter',tparameter);

})();