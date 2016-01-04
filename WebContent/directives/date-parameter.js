 (function(){
'use strict';

/**
 * directive date parameter
- editable Boolean
- parameter {datatype:?? , value, tz, date ....}

 */



var dateParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/date-parameter.html',
		scope:{
			pupdate:'&',
			parameter: '='
		},
		controller: function($scope){
			
			if ("value" in $scope.parameter){
				var myDate=new Date($scope.parameter.value);
				this.dateString = myDate.toLocaleDateString()
				this.newValue=this.dateString;
			}
			
			this.update= function(){
				$scope.parameter.editing=false; 
//				var oldValue=$scope.parameter.value;
				$scope.parameter.value=$scope.parameter.newDate;
				$scope.parameter.date=$scope.parameter.newDate;
				$scope.parameter.tz=new Date().getTimezoneOffset();
				$scope.pupdate({parameter:$scope.parameter});
			}
			
			this.keyUp = function(keyCode) {
				if (keyCode===13) {				// Return key pressed
					thisController.update();
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing=false;			
				}
			}	  
						
		},
		controllerAs: 'dateParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('dateParameter',dateParameter);

})();