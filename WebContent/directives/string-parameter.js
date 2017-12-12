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
			
			
			
			this.blur = function(){
			    $scope.parameter.editing = false;
			    thisController.newValue = $scope.parameter.data.value;
			}
			
			
			
			this.keyUp = function(event) {
				if (event.keyCode === 13) {				// Return key pressed
					$scope.parameter.editing = false; 
					$scope.parameter.data = {value:thisController.newValue};
					$scope.pupdate({parameter:$scope.parameter});
				}
				if (event.keyCode === 27) {		// Escape key pressed
					$scope.parameter.editing = false;
				}
			}
			
			
			
			this.keyDown = function(event) {
				if (event.keyCode === 9) {		// Tab key pressed
					$scope.parameter.editing = false;
					$scope.parameter.data = {value:thisController.newValue};
					$scope.pupdate({parameter:$scope.parameter});
//					console.log("event", event)
//					event.srcElement.next().focus();
				}
			}
			
						
		},
		controllerAs: 'stringParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('stringParameter',stringParameter);

})();