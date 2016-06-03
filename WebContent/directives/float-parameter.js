 (function(){
'use strict';

/**
 * directive float parameter
- editable Boolean
- parameter {datatype:?? , value, tz, timestamp ....}

 */



var floatParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/float-parameter.html',
		scope:{
			pupdate:'&',
			parameter: '='
		},
		controller: function($scope){
			
			if ($scope.parameter.data){
				this.newValue = parseFloat($scope.parameter.data.value);
			}
			
			this.keyUp = function(keyCode) {
				if (keyCode===13) {				// Return key pressed
					$scope.parameter.editing=false; 
					$scope.parameter.data = {value : this.newValue};
					$scope.pupdate({parameter:$scope.parameter});
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing=false;
					this.newValue=$scope.parameter.data.value;
				}
			}
			
			this.keyDown = function(keyCode) {
				if (keyCode===9) {		// Tab key pressed
					$scope.parameter.editing=false; 
					$scope.parameter.data.value=this.newValue;
					$scope.pupdate({parameter:$scope.parameter});
				}
			}
						
		},
		controllerAs: 'floatParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('floatParameter',floatParameter);

})();