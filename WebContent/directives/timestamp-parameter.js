 (function(){
'use strict';

/**
 * directive timestamp parameter
- editable Boolean
- parameter {datatype:?? , value, tz, timestamp ....}

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
			
			var thisController=this;
			
			
			if ("value" in $scope.parameter){
				var myDate=new Date($scope.parameter.value);
				var myTimeString=myDate.toLocaleTimeString(navigator.language,{hour: '2-digit', minute:'2-digit'});
				this.dateString = myDate.toLocaleDateString()+", "+myTimeString;
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
		controllerAs: 'timestampParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('timestampParameter',timestampParameter);

})();