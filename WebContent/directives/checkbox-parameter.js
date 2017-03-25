 (function(){
'use strict';


var checkboxParameter = function() {
	return {
		restrict: 'E',
		replace: true,
		templateUrl: 'directives/checkbox-parameter.html',
		scope:{
			pupdate:'&',
			parameter: '='
		},
		controller: function($scope){
						
			this.change = function() {
				$scope.pupdate({parameter:$scope.parameter});
			};
						
		},
		controllerAs: 'checkboxParamCtrl'
	};
};
    
        
angular.module('unidaplan').directive('checkboxParameter',checkboxParameter);

})();