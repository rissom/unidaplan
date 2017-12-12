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
		controllerAs: 'dateParamCtrl',
		controller: function($scope){
						
			var tc = this;
			
			if ("data" in $scope.parameter){
				tc.date = new Date($scope.parameter.data.date);
				tc.newDate = new Date($scope.parameter.data.date);
			}
			
			
			
			this.getDateString = function(){
				if (tc.date){
					return tc.date.toLocaleDateString();
				} 
				return '-'; // return '-' if no timestamp is defined.
			}
			
			
			this.update = function(){
				$scope.parameter.editing = false;
				$scope.parameter.data = {date : tc.newDate, tz: new Date().getTimezoneOffset() };
				$scope.parameter.date = tc.newDate;
				$scope.parameter.tz = new Date().getTimezoneOffset();
				$scope.pupdate({parameter:$scope.parameter});
				console.log($scope.parameter)
			}
			
			this.keyUp = function(keyCode) {
				if (keyCode===13) {				// Return key pressed
					tc.update();
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing=false;	
					this.newValue = this.dateString;
				}
			}	  
					
			this.keyDown = function(keyCode) {
				if (keyCode===9) {		// Tab key pressed
					tc.update();
				}
			}
			
		},
	};
};
    
        
angular.module('unidaplan').directive('dateParameter',dateParameter);

})();