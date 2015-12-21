 (function(){
'use strict';

/**
 * directive paramfield
 *  TODO:
 * editable Boolean
 */



var paramgrps = function() {
	return {
		restrict: 'E',
		templateUrl: 'directives/paramgrps.html',
		scope:{
			pupdate:'&',
			editable: '@',
			parametergrps: '='
		},
		controller: function($scope){
			this.poptest=function(parameter){
				console.log(parameter)
			}
		},
		controllerAs: 'pgCtrl'
	};
};
    
        
angular.module('unidaplan').directive('paramgrps',paramgrps);

})();