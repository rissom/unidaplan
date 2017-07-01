 (function(){
'use strict';

/**
 * directive string parameter
- editable Boolean
- parameter {datatype:?? , value, tz, timestamp ....}

 */



var stringParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/string-parameter.html',
		scope:{
			pupdate:'&',
			parameter: '='
		},
		controller: function($scope){
			
			var thisController = this;

			this.newValue = $scope.parameter.data ? $scope.parameter.data.value : "";
			
			this.keyUp = function(keyCode) {
				if (keyCode===13) {				// Return key pressed
					$scope.parameter.editing=false; 
					$scope.parameter.data = {value:thisController.newValue};
					$scope.pupdate({parameter:$scope.parameter});
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing=false;			
				}
			}
			
			this.hello = function(){
				console.log("hello");
			}
			
			this.keyDown = function(keyCode) {
				if (keyCode===9) {		// Tab key pressed
					$scope.parameter.editing = false;
					$scope.parameter.data = {value:thisController.newValue};
					$scope.pupdate({parameter:$scope.parameter});	
				}
			}
			
						
		},
		controllerAs: 'stringParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('stringParameter',stringParameter);

})();