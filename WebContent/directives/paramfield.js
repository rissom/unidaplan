 (function(){
'use strict';

/**
 * directive paramfield
- editable Boolean
- parameter {datatype:?? , value, tz, timestamp ....}

 */



var paramfield = function() {
	return {
		restrict: 'E',
		templateUrl: 'directives/paramfield.html',
		scope:{
			pupdate:'&',
			parameters: '='
		},
		controller: function($scope){
			this.keyUp = function(keyCode,newValue,parameter) {
				if (keyCode===13) {				// Return key pressed
					parameter.editing=false; 
					var oldValue=parameter.value;
					parameter.value=newValue;
					$scope.pupdate({parameter:parameter});
//					$scope.pupdate();
				}
				if (keyCode===27) {		// Escape key pressed
					parameter.editing=false;			
				}
			}
						
		},
		controllerAs: 'paramFieldCtrl'
	};
};
    
        
angular.module('unidaplan').directive('paramfield',paramfield);

})();