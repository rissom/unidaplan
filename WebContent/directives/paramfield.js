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
			editable:'=',
			pupdate:'&',
			parameters: '='
		},
		replace: true
	};
};
    
        
angular.module('unidaplan').directive('paramfield',paramfield);

})();