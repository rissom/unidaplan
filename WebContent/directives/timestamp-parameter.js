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
			
			
			if ("value" in $scope.parameter){
				tc.timestamp = new Date($scope.parameter.value);
				tc.newTimestamp = new Date($scope.parameter.value);
			}
			
		
			
			this.getTimestampString = function() {
				// return formatted timestring
				if (tc.timestamp){
					var myTimeString = tc.timestamp.toLocaleTimeString(navigator.language,{hour: '2-digit', minute:'2-digit'});					
					return tc.timestamp.toLocaleDateString() + ", " + myTimeString;
				}
				return '-'; // return '-' if no timestamp is defined.
			}
				
			
			
			this.update= function(){
				tc.timestamp=tc.newTimestamp;
				$scope.parameter.date=tc.timestamp;
				$scope.parameter.value = tc.timestamp.toISOString();
				$scope.parameter.tz=new Date().getTimezoneOffset();
				$scope.parameter.editing=false; 
				$scope.pupdate({parameter:$scope.parameter});
			}
			
			this.keyUp = function(keyCode) {
				if (keyCode===13) {				// Return key pressed
					tc.update();
				}
				if (keyCode===27) {		// Escape key pressed
					$scope.parameter.editing=false;			
				}
			}	  
						
		},
		controllerAs: 'timestampParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('timestampParameter',timestampParameter);

})();