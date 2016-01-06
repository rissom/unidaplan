 (function(){
'use strict';

/**
 * directive string parameter
- editable Boolean
- parameter {datatype:?? , value, tz, timestamp ....}

 */



var emailParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/email-parameter.html',
		scope:{
			pupdate:'&',
			parameter: '='
		},
		controller: function($scope){
			
			var thisController=this;
			
			this.newValue=$scope.parameter.value;

			this.keyUp = function(keyCode) {
				if (keyCode===13) {				// Return key pressed
					$scope.parameter.editing=false; 
					var oldValue=$scope.parameter.value;
					$scope.parameter.value=thisController.newValue;
					$scope.pupdate({parameter:$scope.parameter});
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing=false;			
				}
			}
						
		},
		controllerAs: 'emailParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('emailParameter',emailParameter);

})();