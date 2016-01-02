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
		controllerAs: 'floatParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('floatParameter',floatParameter);

})();