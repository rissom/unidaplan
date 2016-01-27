 (function(){
'use strict';

/**
 * directive chooser parameter
- editable Boolean
- parameter {datatype:?? , value, tz, timestamp ....}

 */



var chooserParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/chooser-parameter.html',
		scope:{
			pupdate:'&',
			parameter: '='
		},
		controller: function($scope){
			
			var thisController=this;

			this.newValue=$scope.parameter.value;
			
			this.keyUp = function(keyCode) {
				if (keyCode===13) {				// Return key pressed
					thisController.update();
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing=false;			
				}
			}
			
			this.update = function() {
				$scope.parameter.editing=false; 
				$scope.parameter.value=thisController.newValue;
				$scope.pupdate({parameter:$scope.parameter});
			}
						
		},
		controllerAs: 'chooserParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('chooserParameter',chooserParameter);

})();