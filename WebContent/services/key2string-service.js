(function(){
'use strict';

var key2stringService = function($translate){

	
	// get the translated string for a string key
	this.key2string = function(stringkey,strings) {
		var keyfound=false;
		var returnString="@@@ no string! @@@";
		angular.forEach(strings, function(translation) {
			if (!keyfound && stringkey==translation.string_key) {
				returnString = translation.value;
				if (translation.language==$translate.use()) {
					keyfound=true;
				}
			}
		})
		return returnString;
	};
	
}


angular.module('unidaplan').service('key2string', ['$translate',key2stringService]);

})();