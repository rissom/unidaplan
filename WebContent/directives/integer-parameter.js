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
			
		    console.log ("scope-parameter",$scope.parameter);
		    if ($scope.parameter.data && $scope.parameter.data.value){
		        this.newValue = $scope.parameter.data.value;
		    }
						
			this.keyUp = function(keyCode) {
				if (keyCode === 13) {				// Return key pressed
					$scope.parameter.editing = false; 
				    if (this.form.$valid){
                        $scope.parameter.data = {"value" : this.newValue};
                        $scope.pupdate({parameter:$scope.parameter});
                    }    
				}
				if (keyCode === 27) {		// Escape key pressed
					$scope.parameter.editing=false;			
				}
			}
			
			this.keyDown = function(keyCode) {
				if (keyCode === 9) {		// Tab key pressed
					$scope.parameter.editing = false; 
					$scope.parameter.data.value = this.newValue;
					$scope.pupdate({parameter:$scope.parameter});
				}
			}
						
		},
		controllerAs: 'integerParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('integerParameter',integerParameter);

})();