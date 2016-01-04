 (function(){
'use strict';

/**
 * directive measurement parameter
- editable Boolean
- parameter {datatype:?? , value, tz, timestamp ....}

 */



var measurementParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/measurement-parameter.html',
		scope:{
			pupdate:'&',
			parameter: '='
		},
		controller: function($scope){
			
			if ($scope.parameter.value!=undefined) {
				this.newValue=$scope.parameter.value.split("±")[0];
				this.newError=$scope.parameter.value.split("±")[1];
			}
			
			this.keyUp = function(keyCode) {
				if (keyCode===13) {				// Return key pressed
					$scope.parameter.editing=false;
					var oldValue=$scope.parameter.value;
					$scope.parameter.value=this.newValue;
					$scope.parameter.error=this.newError;
					$scope.pupdate({parameter:$scope.parameter});
//					$scope.pupdate();
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing=false;
					this.newValue=$scope.parameter.value.split("±")[0];
					this.newError=$scope.parameter.value.split("±")[1];
				}
			}
						
		},
		controllerAs: 'measurementParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('measurementParameter',measurementParameter);

})();