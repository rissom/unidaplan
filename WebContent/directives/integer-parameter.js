 (function(){
'use strict';

/**
 * directive integer parameter
- editable Boolean
- parameter {datatype:?? , value, tz, timestamp ....}

 */



var integerParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/integer-parameter.html',
		scope:{
			pupdate:'&',
			parameter: '='
		},
		controller: function($scope){
			
			this.newValue=$scope.parameter.value;
			
			this.keyUp = function(keyCode) {
				if (keyCode===13) {				// Return key pressed
					$scope.parameter.editing=false; 
					var oldValue=$scope.parameter.value;
					$scope.parameter.value=this.newValue;
					$scope.pupdate({parameter:$scope.parameter});
//					$scope.pupdate();
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing=false;			
				}
			}
						
		},
		controllerAs: 'integerParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('integerParameter',integerParameter);

})();