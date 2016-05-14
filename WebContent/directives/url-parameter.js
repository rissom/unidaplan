 (function(){
'use strict';

/**
 * directive string parameter
- editable Boolean
- parameter {datatype:?? , value, tz, timestamp ....}

 */



var urlParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/url-parameter.html',
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
			
			this.keyDown = function(keyCode) {
				if (keyCode===9) {		// Tab key pressed
					$scope.parameter.editing=false; 
					var oldValue=$scope.parameter.value;
					$scope.parameter.value=thisController.newValue;
					$scope.pupdate({parameter:$scope.parameter});
				}
			}
						
		},
		controllerAs: 'urlParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('urlParameter',urlParameter);

angular.module('unidaplan').directive('validurl', function() {
	return {
	    require: 'ngModel',
	    link: function(scope, elm, attrs, ctrl) {
	    	ctrl.$validators.integer = function(modelValue, viewValue) {
	    		return re_weburl.exec(viewValue);
      	  	};
	    }
	};
});

})();