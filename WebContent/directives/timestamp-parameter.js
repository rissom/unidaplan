 (function(){
'use strict';

/**
 * directive timestamp parameter
Dataformat for parameter:

 {value (String from JSON), 
 	tz (Integer, offset to UTC in min)
 	timestamp (Date object)}

 */



var timestampParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/timestamp-parameter.html',
		scope:{
			pupdate:'&',
			parameter: '='
		},
		controller: function($scope){
			
			var tc=this;
			
			
			if ("data" in $scope.parameter){
				tc.timestamp = new Date($scope.parameter.data.date);
				tc.newTimestamp = new Date($scope.parameter.data.date);
			}
			
		
			
			this.getTimestampString = function() {
				// return formatted timestring
				if (tc.timestamp){
					var myTimeString = tc.timestamp.toLocaleTimeString(navigator.language,{hour: '2-digit', minute:'2-digit'});					
					return tc.timestamp.toLocaleDateString() + ", " + myTimeString;
				}
				return '-'; // return '-' if no timestamp is defined.
			}
				
			
			
			this.update = function(){
				tc.timestamp = tc.newTimestamp;
				$scope.parameter.date = tc.timestamp;
				$scope.parameter.tz = new Date().getTimezoneOffset();
				$scope.parameter.data = { date : tc.timestamp.toISOString(), 
										  tz : $scope.parameter.tz};
				$scope.parameter.editing = false; 
				$scope.pupdate({parameter:$scope.parameter});
			}
			
			this.keyUp = function(event) {
				if (event.keyCode === 13) {				// Return key pressed
					tc.update();
				}
				if (event.keyCode === 27) {		// Escape key pressed
					$scope.parameter.editing = false;			
				}
			}
			
			this.keyDown = function(event) {
				if (event.keyCode === 9) {		// Tab key pressed
					tc.update();
				}
			}
						
		},
		controllerAs: 'timestampParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('timestampParameter',timestampParameter);

})();