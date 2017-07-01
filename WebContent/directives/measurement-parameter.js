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
			
			if ($scope.parameter.data != undefined) {
				this.newValue = $scope.parameter.data.value;
				this.newError = $scope.parameter.data.error;
			}
			
			this.keyDown = function(keyCode) {
				if (keyCode===9) {		// Tab key pressed
					$scope.parameter.editing = false;
					if ($scope.parameter.data == undefined){
						$scope.parameter.data = {value : this.newValue,
												 error : this.newError};
					} else{
						$scope.parameter.data.value = this.newValue;
						$scope.parameter.data.error = this.newError;
					}
					$scope.parameter.data.error = this.newError;
					$scope.pupdate({parameter:$scope.parameter});
				}
			}
			
			this.keyUp = function(keyCode) {
				if (keyCode===13) {				// Return key pressed
					$scope.parameter.editing = false;
					$scope.parameter.data = {"value" : this.newValue, "error" : this.newError};
					$scope.pupdate({parameter:$scope.parameter});
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing = false;
					this.newValue = $scope.parameter.data.value;
					this.newError = $scope.parameter.data.error;
				}
			}
						
		},
		controllerAs: 'measurementParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('measurementParameter',measurementParameter);

})();