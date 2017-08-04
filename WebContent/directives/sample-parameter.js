 (function(){
'use strict';

/**
 * directive sample parameter

 */



var sampleParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/sample-parameter.html',
		scope:{
			pupdate:   '&',
			parameter: '='
		},
		controller: function($scope){
			
			var thisController = this;

//			parameter.data.name = parameter.possiblesamples.filter(function (x){return x.id == parameter.data.id})
			
			if ( $scope.parameter.hasOwnProperty("data") && $scope.parameter.data.hasOwnProperty("id") ) {
				this.newValue = $scope.parameter.possiblesamples.filter(function (x){return x.id == $scope.parameter.data.id})[0];
			}
			
			$scope.parameter.possiblesamples.push({name:"none"})
						
			this.blur = function(){
				$scope.parameter.editing = false;
			}
			
			this.keyUp = function(keyCode) {
				if (keyCode === 13) {				// Return key pressed
					thisController.update();
				}
				if (keyCode === 27) {		// Escape key pressed
					$scope.parameter.editing = false;			
				}
			}
			
			this.update = function() {
				$scope.parameter.editing = false; 
				$scope.parameter.data = {id:thisController.newValue.id};
				$scope.pupdate({parameter:$scope.parameter});
			}
						
		},
		controllerAs: 'sampleParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('sampleParameter',sampleParameter);

})();