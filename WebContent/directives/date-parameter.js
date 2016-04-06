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
			
			var tc=this;
			
			if ("value" in $scope.parameter){
				tc.date = new Date($scope.parameter.value);
				tc.newDate = new Date($scope.parameter.value);
			}
			
			
			
			this.getDateString = function(){
				tc.dateString = myDate.toLocaleDateString()
				this.newValue=this.dateString;
				if (tc.date){
					return tc.date.toLocaleDateString();
				}
				return '-'; // return '-' if no timestamp is defined.
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
					tc.update();
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing=false;	
					this.newValue=this.dateString;
				}
			}	  
						
		},
		controllerAs: 'dateParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('dateParameter',dateParameter);

})();